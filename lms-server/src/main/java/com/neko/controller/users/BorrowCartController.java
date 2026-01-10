package com.neko.controller.users;

import com.neko.context.BaseContext;
import com.neko.dto.BorrowCartDTO;
import com.neko.entity.BorrowCart;
import com.neko.result.Result;
import com.neko.service.BorrowCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/borrowCart")
@Slf4j
public class BorrowCartController {

    private final BorrowCartService borrowCartService;

    public BorrowCartController(BorrowCartService borrowCartService) {
        this.borrowCartService = borrowCartService;
    }

    @PostMapping("/add")
    public Result<Object> add(@RequestBody BorrowCartDTO borrowCartDTO) {
        log.info("Add borrow cart, item info: {}", borrowCartDTO);
        borrowCartService.addOrUpdate(borrowCartDTO, true);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<BorrowCart>> list() {
        List<BorrowCart> list = borrowCartService.show();
        return Result.success(list);
    }

    @DeleteMapping("/clean")
    public Result<Object> clean() {
        log.info("Clean borrow cart");
        borrowCartService.clean();
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Object> deleteById(@PathVariable Long id) {
        log.info("Delete borrow cart, id: {}", id);
        BorrowCart borrowCart = new BorrowCart();
        borrowCart.setBookId(id);
        Long userId = BaseContext.getCurrentId();
        borrowCart.setUserId(userId);

        borrowCartService.delete(borrowCart);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Object> update(@RequestBody BorrowCartDTO borrowCartDTO) {
        log.info("Update borrow cart, item info: {}", borrowCartDTO);
        if (borrowCartDTO.getNumber() <= 0) {
            return Result.error("number is not positive");
        }
        borrowCartService.addOrUpdate(borrowCartDTO, false);
        return Result.success();
    }
}
