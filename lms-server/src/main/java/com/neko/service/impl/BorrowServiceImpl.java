package com.neko.service.impl;

import com.neko.constant.MessageConstant;
import com.neko.context.BaseContext;
import com.neko.dto.BorrowPageQueryDTO;
import com.neko.dto.BorrowSubmitDTO;
import com.neko.entity.Book;
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
import com.neko.mapper.BookMapper;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class BorrowServiceImpl implements BorrowService {
    private final BorrowCartMapper borrowCartMapper;
    private final BorrowRecordMapper borrowRecordMapper;
    private final BorrowDetailMapper borrowDetailMapper;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public BorrowServiceImpl(BorrowCartMapper borrowCartMapper,
            BorrowRecordMapper borrowRecordMapper, BorrowDetailMapper borrowDetailMapper,
            UserMapper userMapper, BookMapper bookMapper) {
        this.borrowCartMapper = borrowCartMapper;
        this.borrowRecordMapper = borrowRecordMapper;
        this.borrowDetailMapper = borrowDetailMapper;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    @Override
    public BorrowSubmitVO borrow(BorrowSubmitDTO borrowSubmitDTO) {
        // 查询借阅车数据
        Long userId = BaseContext.getCurrentId();
        BorrowCart borrowCart = new BorrowCart();
        borrowCart.setUserId(userId);
        List<BorrowCart> allCartItems = borrowCartMapper.list(borrowCart);

        if (allCartItems == null || allCartItems.isEmpty()) {
            throw new BorrowCartBusinessException(MessageConstant.BORROW_CART_IS_NULL);
        }

        // 根据参数决定要借阅的图书列表
        List<BorrowCart> itemsToBorrow;
        boolean borrowAll = (borrowSubmitDTO == null || borrowSubmitDTO.getBookIds() == null
                || borrowSubmitDTO.getBookIds().isEmpty());

        if (borrowAll) {
            // 借阅全部
            itemsToBorrow = allCartItems;
        } else {
            // 选择性借阅
            itemsToBorrow = allCartItems.stream()
                    .filter(item -> borrowSubmitDTO.getBookIds().contains(item.getBookId()))
                    .collect(Collectors.toList());

            if (itemsToBorrow.isEmpty()) {
                throw new BorrowCartBusinessException("选中的图书不在借阅车中");
            }
        }

        // 查询用户信息
        User user = userMapper.getById(userId);

        // 插入借阅记录数据
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setBorrowTime(LocalDateTime.now());
        borrowRecord.setStatus(BorrowStatus.BORROWED.getCode());
        borrowRecord.setNumber(String.valueOf(System.currentTimeMillis()));
        borrowRecord.setUserId(userId);
        borrowRecord.setUserName(user.getUsername());
        borrowRecord.setDueDate(LocalDateTime.now().plusMonths(1)); // 设置到期时间为1个月后
        borrowRecord.setRenewCount(0); // 初始续借次数为0

        borrowRecordMapper.insert(borrowRecord);

        // 检查所有图书库存是否足够
        for (BorrowCart cart : itemsToBorrow) {
            Book book = bookMapper.getById(cart.getBookId());
            if (book == null) {
                throw new BorrowBusinessException(String.format(MessageConstant.BOOK_NOT_FOUND, cart.getName()));
            }
            if (book.getStock() == null || book.getStock() < cart.getNumber()) {
                throw new BorrowBusinessException(String.format(MessageConstant.BOOK_STOCK_NOT_ENOUGH,
                        cart.getName(), book.getStock() == null ? 0 : book.getStock(), cart.getNumber()));
            }
        }

        // 创建借阅明细并减少库存
        List<BorrowDetail> borrowDetailList = new ArrayList<>();
        for (BorrowCart cart : itemsToBorrow) {
            BorrowDetail borrowDetail = new BorrowDetail();
            BeanUtils.copyProperties(cart, borrowDetail);
            borrowDetail.setBorrowRecordId(borrowRecord.getId());
            borrowDetailList.add(borrowDetail);

            // 减少图书库存，使用 BorrowCart 中的数量
            bookMapper.updateStock(cart.getBookId(), -cart.getNumber());
        }

        borrowDetailMapper.insertBatch(borrowDetailList);

        // 根据借阅模式删除借阅车中的图书
        if (borrowAll) {
            // 借阅全部，清空借阅车
            borrowCartMapper.deleteByUserId(userId);
        } else {
            // 选择性借阅，只删除已借阅的图书
            borrowCartMapper.deleteByUserIdAndBookIds(userId, borrowSubmitDTO.getBookIds());
        }

        return BorrowSubmitVO.builder()
                .id(borrowRecord.getId())
                .borrowTime(borrowRecord.getBorrowTime())
                .borrowNumber(borrowRecord.getNumber())
                .build();
    }

    @Override
    public PageResult<BorrowVO> pageQuery4User(BorrowPageQueryDTO borrowPageQueryDTO) {
        // 设置分页
        borrowPageQueryDTO.setUserId(BaseContext.getCurrentId());
        borrowPageQueryDTO.setOffset((borrowPageQueryDTO.getPage() - 1) * borrowPageQueryDTO.getPageSize());

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

        // 判断是否逾期（超过到期时间）
        LocalDateTime dueDate = borrowRecordDB.getDueDate();
        LocalDateTime returnTime = LocalDateTime.now();
        boolean isOverdue = dueDate != null && returnTime.isAfter(dueDate);

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

        // 更新借阅记录状态为已归还
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setId(borrowRecordDB.getId());
        borrowRecord.setStatus(BorrowStatus.RETURNED.getCode());
        borrowRecord.setReturnTime(returnTime);
        borrowRecordMapper.update(borrowRecord);

        // 查询借阅详情，增加图书库存
        List<BorrowDetail> borrowDetailList = borrowDetailMapper.getByBorrowRecordId(id);
        for (BorrowDetail borrowDetail : borrowDetailList) {
            // 归还图书，增加库存，使用 BorrowDetail 中的数量
            bookMapper.updateStock(borrowDetail.getBookId(), borrowDetail.getNumber());
        }
    }

    @Override
    public boolean renew(Long id) {
        // 根据 id 查询借阅记录
        BorrowRecord borrowRecordDB = borrowRecordMapper.getById(id);

        if (borrowRecordDB == null ||
                !borrowRecordDB.getStatus().equals(BorrowStatus.BORROWED.getCode())) {
            throw new BorrowBusinessException(MessageConstant.BORROW_STATUS_ERROR);
        }

        // 获取到期时间
        LocalDateTime dueDate = borrowRecordDB.getDueDate();
        if (dueDate == null) {
            throw new BorrowBusinessException(MessageConstant.BORROW_DUE_DATE_NULL);
        }

        // 判断是否可以续借：剩余时间在7天内
        if (dueDate.isAfter(LocalDateTime.now().plusDays(7))) {
            // 剩余时间超过7天，无法续借
            log.info("续借失败：借阅记录 id={}, 剩余时间超过7天，无法续借", id);
            return false;
        }

        // 可以续借，延长到期时间1个月
        BorrowRecord updateRecord = new BorrowRecord();
        updateRecord.setId(id);
        updateRecord.setDueDate(borrowRecordDB.getDueDate().plusMonths(1));
        updateRecord.setRenewCount((borrowRecordDB.getRenewCount() == null ? 0 : borrowRecordDB.getRenewCount()) + 1);

        borrowRecordMapper.update(updateRecord);
        log.info("续借成功，借阅记录 ID: {}, 新的到期时间: {}", id, updateRecord.getDueDate());

        return true;
    }
}
