package com.neko.controller.users;

import com.neko.dto.BorrowPageQueryDTO;
import com.neko.result.PageResult;
import com.neko.result.Result;
import com.neko.service.BorrowService;
import com.neko.vo.BorrowSubmitVO;
import com.neko.vo.BorrowVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/borrow")
@Slf4j
public class UserBorrowController {

    private final BorrowService borrowService;

    public UserBorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    /**
     * 借书
     * 状态：BORROWED
     */
    @PostMapping("/borrow")
    public Result<BorrowSubmitVO> borrow() {
        log.info("User borrow book");
        BorrowSubmitVO borrowSubmitVO = borrowService.borrow();
        return Result.success(borrowSubmitVO);
    }

    /**
     * 借阅历史查询
     *
     * @param borrowPageQueryDTO
     * @return
     */
    @GetMapping("/history")
    public Result<PageResult<BorrowVO>> page(BorrowPageQueryDTO borrowPageQueryDTO) {
        log.info("user search borrow history: {}", borrowPageQueryDTO);
        PageResult<BorrowVO> pageResult = borrowService.pageQuery4User(borrowPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 借阅详情
     *
     * @param id
     * @return
     */
    @GetMapping("/detail/{id}")
    public Result<BorrowVO> detail(@PathVariable Long id) {
        BorrowVO borrowVO = borrowService.detail(id);
        return Result.success(borrowVO);
    }

    /**
     * 完成归还
     * 状态变化：BORROWED/OVERDUE -> RETURNED
     *
     * @return
     */
    @PutMapping("/complete/{id}")
    public Result<Object> complete(@PathVariable Long id) {
        borrowService.complete(id);
        return Result.success();
    }

    /**
     * 续借
     * 要求：剩余借阅时间在7天内才可以续借，续借延长1个月
     *
     * @param id 借阅记录id
     * @return 续借结果
     */
    @PutMapping("/renew/{id}")
    public Result<Boolean> renew(@PathVariable Long id) {
        log.info("用户续借，借阅记录 id: {}", id);
        boolean success = borrowService.renew(id);
        return Result.success(success);
    }
}
