package com.neko.controller.admin;

import com.neko.dto.UserPageQueryDTO;
import com.neko.result.PageResult;
import com.neko.result.Result;
import com.neko.service.UserService;
import com.neko.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/user")
@Slf4j
public class AdminUserController {

    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;

    public AdminUserController(UserService userService, RedisTemplate<String, Object> redisTemplate) {
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/page")
    public Result<PageResult<UserVO>> page(UserPageQueryDTO userPageQueryDTO) {
        log.info("Paginated query of user(s): {}", userPageQueryDTO);
        PageResult<UserVO> pageResult = userService.pageQuery(userPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    public Result<Object> delete(@RequestParam List<Long> ids) {
        log.info("delete user(s): {}", ids);
        userService.deleteBatch(ids);

        cleanCache("*user_*");
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        log.info("admin get user by id: {}", id);
        UserVO userVO = userService.getById(id);
        return Result.success(userVO);
    }

    @PostMapping("/status/{status}")
    public Result<Object> updateStatus(@PathVariable Integer status, Long id) {
        log.info("admin update user status: {}, user id: {}", status, id);
        userService.setStatus(status, id);
        cleanCache("*user_*");
        return Result.success();
    }

    private void cleanCache(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
