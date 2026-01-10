package com.neko.controller.admin;

import com.neko.dto.BookDTO;
import com.neko.dto.BookPageQueryDTO;
import com.neko.entity.Book;
import com.neko.result.PageResult;
import com.neko.result.Result;
import com.neko.service.BookService;
import com.neko.vo.BookVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/book")
@Slf4j
public class BookController {

    private final BookService bookService;
    private final RedisTemplate<String, Object> redisTemplate;

    public BookController(BookService bookService, RedisTemplate<String, Object> redisTemplate) {
        this.bookService = bookService;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping
    public Result<Object> save(@RequestBody BookDTO bookDTO) {
        log.info("New book added, {}", bookDTO);
        bookService.save(bookDTO);

        String key = "book_" + bookDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult<BookVO>> page(BookPageQueryDTO bookPageQueryDTO) throws IOException {
        log.info("Paginated query of book(s): {}", bookPageQueryDTO);
        PageResult<BookVO> pageResult = bookService.pageQuery(bookPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    public Result<Object> delete(@RequestParam List<Long> ids) {
        log.info("delete book(s): {}", ids);
        bookService.deleteBatch(ids);

        cleanCache("*book_*");
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<BookVO> getById(@PathVariable Long id) {
        log.info("admin get book by id: {}", id);
        BookVO bookVO = bookService.getById(id);
        return Result.success(bookVO);
    }

    @PutMapping
    public Result<Object> update(@RequestBody BookDTO bookDTO) {
        log.info("update book: {}", bookDTO);
        bookService.update(bookDTO);

        cleanCache("*book_*");
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result<Object> updateStatus(@PathVariable Integer status, Long id) {
        log.info("admin update book status: {}, book id: {}", status, id);
        bookService.setStatus(status, id);
        cleanCache("*book_*");
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Book>> list(Long categoryId) {
        List<Book> bookList = bookService.list(categoryId);
        return Result.success(bookList);
    }

    private void cleanCache(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
