package com.neko.service.impl;

import com.neko.constant.MessageConstant;
import com.neko.context.BaseContext;
import com.neko.dto.BorrowPageQueryDTO;
import com.neko.entity.BorrowDetail;
import com.neko.entity.BorrowRecord;
import com.neko.entity.BorrowCart;
import com.neko.entity.User;
import com.neko.enums.BorrowStatus;
import com.neko.exception.BorrowBusinessException;
import com.neko.exception.BorrowCartBusinessException;
import com.neko.mapper.BorrowDetailMapper;
import com.neko.mapper.BorrowRecordMapper;
import com.neko.mapper.BorrowCartMapper;
import com.neko.mapper.UserMapper;
import com.neko.result.PageResult;
import com.neko.service.BorrowService;
import com.neko.vo.BorrowSubmitVO;
import com.neko.vo.BorrowVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BorrowServiceImpl implements BorrowService {
    private final BorrowCartMapper borrowCartMapper;
    private final BorrowRecordMapper borrowRecordMapper;
    private final BorrowDetailMapper borrowDetailMapper;
    private final UserMapper userMapper;

    public BorrowServiceImpl(BorrowCartMapper borrowCartMapper,
            BorrowRecordMapper borrowRecordMapper, BorrowDetailMapper borrowDetailMapper,
            UserMapper userMapper) {
        this.borrowCartMapper = borrowCartMapper;
        this.borrowRecordMapper = borrowRecordMapper;
        this.borrowDetailMapper = borrowDetailMapper;
        this.userMapper = userMapper;
    }

    @Override
    public BorrowSubmitVO borrow() {
        // 查询借阅车数据
        Long userId = BaseContext.getCurrentId();
        BorrowCart borrowCart = new BorrowCart();
        borrowCart.setUserId(userId);
        List<BorrowCart> list = borrowCartMapper.list(borrowCart);
        if (list == null || list.isEmpty()) {
            throw new BorrowCartBusinessException(MessageConstant.BORROW_CART_IS_NULL);
        }

        // 插入数据
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setBorrowTime(LocalDateTime.now());
        borrowRecord.setStatus(BorrowStatus.BORROWED.getCode());
        borrowRecord.setNumber(String.valueOf(System.currentTimeMillis()));
        borrowRecord.setUserId(userId);

        borrowRecordMapper.insert(borrowRecord);

        List<BorrowDetail> borrowDetailList = new ArrayList<>();
        for (BorrowCart cart : list) {
            BorrowDetail borrowDetail = new BorrowDetail();
            BeanUtils.copyProperties(cart, borrowDetail);
            borrowDetail.setBorrowRecordId(borrowRecord.getId());
            borrowDetailList.add(borrowDetail);
        }

        borrowDetailMapper.insertBatch(borrowDetailList);

        borrowCartMapper.deleteByUserId(userId);

        return BorrowSubmitVO.builder()
                .id(borrowRecord.getId())
                .borrowTime(borrowRecord.getBorrowTime())
                .borrowNumber(borrowRecord.getNumber())
                .build();
    }

    @Override
    public PageResult<BorrowVO> pageQuery4User(int pageNum, int pageSize, Integer status) {
        // 设置分页
        BorrowPageQueryDTO borrowPageQueryDTO = new BorrowPageQueryDTO();
        borrowPageQueryDTO.setUserId(BaseContext.getCurrentId());
        borrowPageQueryDTO.setStatus(status);
        borrowPageQueryDTO.setPage(pageNum);
        borrowPageQueryDTO.setPageSize(pageSize);
        borrowPageQueryDTO.setOffset((pageNum - 1) * pageSize);

        // 分页条件查询
        Long total = borrowRecordMapper.count(borrowPageQueryDTO);
        List<BorrowRecord> page = borrowRecordMapper.pageQuery(borrowPageQueryDTO);

        List<BorrowVO> list = new ArrayList<>();

        // 查询出借阅明细，并封装入BorrowVO进行响应
        if (page != null && !page.isEmpty()) {
            for (BorrowRecord borrowRecord : page) {
                Long borrowRecordId = borrowRecord.getId();// 借阅记录 id

                // 查询借阅明细
                List<BorrowDetail> borrowDetails = borrowDetailMapper.getByBorrowRecordId(borrowRecordId);

                BorrowVO borrowVO = new BorrowVO();
                BeanUtils.copyProperties(borrowRecord, borrowVO);
                borrowVO.setBorrowDetailList(borrowDetails);

                list.add(borrowVO);
            }
        }
        return new PageResult<>(total, list);
    }

    @Override
    public BorrowVO detail(Long id) {
        // 根据 id 查询借阅记录
        BorrowRecord borrowRecord = borrowRecordMapper.getById(id);

        // 查询该借阅对应的书本明细
        List<BorrowDetail> borrowDetailList = borrowDetailMapper.getByBorrowRecordId(borrowRecord.getId());

        // 将该借阅及其详情封装到 BorrowVO 并返回
        BorrowVO borrowVO = new BorrowVO();
        BeanUtils.copyProperties(borrowRecord, borrowVO);
        borrowVO.setBorrowDetailList(borrowDetailList);

        return borrowVO;
    }

    public PageResult<BorrowVO> conditionSearch(BorrowPageQueryDTO borrowPageQueryDTO) {
        borrowPageQueryDTO.setOffset((borrowPageQueryDTO.getPage() - 1) * borrowPageQueryDTO.getPageSize());

        Long total = borrowRecordMapper.count(borrowPageQueryDTO);
        List<BorrowRecord> page = borrowRecordMapper.pageQuery(borrowPageQueryDTO);

        List<BorrowVO> borrowVOList = getBorrowVOList(page);

        return new PageResult<>(total, borrowVOList);
    }

    private List<BorrowVO> getBorrowVOList(List<BorrowRecord> page) {
        return page.stream()
                .map(borrowRecord -> {
                    BorrowVO vo = new BorrowVO();
                    BeanUtils.copyProperties(borrowRecord, vo);
                    return vo;
                })
                .toList();
    }

    @Override
    public void complete(Long id) {
        // 根据 id 查询借阅记录
        BorrowRecord borrowRecordDB = borrowRecordMapper.getById(id);

        if (borrowRecordDB == null ||
                !borrowRecordDB.getStatus().equals(BorrowStatus.BORROWED.getCode())) {
            throw new BorrowBusinessException(MessageConstant.BORROW_STATUS_ERROR);
        }

        // 判断是否逾期（借阅超过1个月）
        LocalDateTime borrowTime = borrowRecordDB.getBorrowTime();
        LocalDateTime returnTime = LocalDateTime.now();
        boolean isOverdue = returnTime.isAfter(borrowTime.plusMonths(1));

        // 如果逾期，更新用户逾期次数
        if (isOverdue) {
            Long userId = borrowRecordDB.getUserId();
            // 查询用户信息
            User user = userMapper.getById(userId);
            int overdueCount = user.getOverdueCount() == null ? 0 : user.getOverdueCount();
            overdueCount++;
            
            // 如果逾期次数达到2次，封禁账号
            if (overdueCount >= 2) {
                user.setStatus(0); // 0表示禁用
            }
            user.setOverdueCount(overdueCount);
            userMapper.update(user);
        }

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setId(borrowRecordDB.getId());
        borrowRecord.setStatus(BorrowStatus.RETURNED.getCode());
        borrowRecord.setReturnTime(returnTime);

        borrowRecordMapper.update(borrowRecord);
    }
}
