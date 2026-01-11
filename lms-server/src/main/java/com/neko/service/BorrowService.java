package com.neko.service;

import com.neko.dto.BorrowPageQueryDTO;
import com.neko.result.PageResult;
import com.neko.vo.BorrowSubmitVO;
import com.neko.vo.BorrowVO;

public interface BorrowService {
    BorrowSubmitVO borrow();

    /**
     * 用户端借阅分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    PageResult<BorrowVO> pageQuery4User(int pageNum, int pageSize, Integer status);

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
