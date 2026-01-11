package com.neko.service;

import com.neko.dto.BorrowPageQueryDTO;
import com.neko.dto.BorrowSubmitDTO;
import com.neko.result.PageResult;
import com.neko.vo.BorrowSubmitVO;
import com.neko.vo.BorrowVO;

import java.util.List;

public interface BorrowService {
    /**
     * 借阅图书
     * 
     * @param borrowSubmitDTO 可选参数，如果传入则只借阅指定的图书，如果为null则借阅车中所有图书
     * @return 借阅提交结果
     */
    BorrowSubmitVO borrow(BorrowSubmitDTO borrowSubmitDTO);

    /**
     * 用户端借阅分页查询
     *
     * @param borrowPageQueryDTO
     * @return
     */
    PageResult<BorrowVO> pageQuery4User(BorrowPageQueryDTO borrowPageQueryDTO);

    /**
     * 查询借阅详情
     *
     * @param id
     * @return
     */
    BorrowVO detail(Long id);

    /**
     * 条件搜索借阅记录
     *
     * @param borrowPageQueryDTO
     * @return
     */
    PageResult<BorrowVO> conditionSearch(BorrowPageQueryDTO borrowPageQueryDTO);

    /**
     * 完成归还
     *
     * @param id
     */
    void complete(Long id);

    /**
     * 续借
     *
     * @param id 借阅记录 id
     * @return true 续借成功，false 续借失败
     */
    boolean renew(Long id);
}
