package com.neko.controller.admin;

import com.neko.dto.BorrowPageQueryDTO;
import com.neko.result.PageResult;
import com.neko.result.Result;
import com.neko.service.BorrowService;
import com.neko.vo.BorrowVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/borrow")
@Slf4j
public class AdminBorrowController {

    private final BorrowService borrowService;

    public AdminBorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @GetMapping("/conditionSearch")
    public Result<PageResult<BorrowVO>> conditionSearch(BorrowPageQueryDTO borrowPageQueryDTO) {
        log.info("Admin search borrow record(s): {}", borrowPageQueryDTO);
        PageResult<BorrowVO> pageResult = borrowService.conditionSearch(borrowPageQueryDTO);
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
}
