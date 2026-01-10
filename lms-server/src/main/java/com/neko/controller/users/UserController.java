package com.neko.controller.users;

import com.neko.constant.JwtClaimsConstant;
import com.neko.constant.MessageConstant;
import com.neko.dto.UserCodeDTO;
import com.neko.dto.UserDTO;
import com.neko.dto.UserPasswordDTO;
import com.neko.entity.User;
import com.neko.exception.AccountNotFoundException;
import com.neko.properties.JwtProperties;
import com.neko.result.Result;
import com.neko.service.MailService;
import com.neko.service.UserService;
import com.neko.utils.CaptchaUtil;
import com.neko.utils.JwtUtil;
import com.neko.vo.UserLoginVO;
import com.neko.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    private final JwtProperties jwtProperties;
    private final MailService mailService;

    public UserController(UserService userService, JwtProperties jwtProperties, MailService mailService) {
        this.userService = userService;
        this.jwtProperties = jwtProperties;
        this.mailService = mailService;
    }

    @PostMapping("/register")
    public Result<UserLoginVO> register(@RequestBody UserPasswordDTO userPasswordDTO) {
        log.info("User register: {}", userPasswordDTO);
        User user = userService.register(userPasswordDTO);
        UserLoginVO userLoginVO = getUserVO(user);
        return Result.success(userLoginVO);
    }

    @PostMapping("/login/code")
    public Result<UserLoginVO> login(@RequestBody UserCodeDTO userCodeDTO) {
        log.info("User login by code: {}", userCodeDTO);
        User user = userService.login(userCodeDTO);
        UserLoginVO userLoginVO = getUserVO(user);
        return Result.success(userLoginVO);
    }

    @PostMapping("/login/password")
    public Result<UserLoginVO> login(@RequestBody UserPasswordDTO userPasswordDTO) {
        log.info("User login by password: {}", userPasswordDTO);
        User user = userService.login(userPasswordDTO);
        UserLoginVO userLoginVO = getUserVO(user);
        return Result.success(userLoginVO);
    }

    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    @PostMapping("/login/send")
    public Result<Object> sendCode(@RequestParam String email) {
        mailService.sendCode(email);
        return Result.success();
    }

    @PostMapping("/login/verify")
    public Result<Object> verifyCode(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        // 校验阿里云图形验证码
        if (!CaptchaUtil.verifyCaptcha(code, System.getenv("SCENE_ID"))) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        return Result.success();
    }

    private UserLoginVO getUserVO(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);

        return UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .token(token)
                .build();
    }

    @GetMapping("/profile")
    public Result<UserVO> getById(@RequestParam long id) {
        log.info("get user profile, {}", id);
        UserVO userVO = userService.getById(id);
        return Result.success(userVO);
    }

    @PutMapping("/profile")
    public Result<Object> update(@RequestBody UserDTO userDTO) {
        log.info("update user profile: {}", userDTO);
        userService.update(userDTO);
        return Result.success();
    }
}
