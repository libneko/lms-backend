package com.neko.controller.admin;

import com.neko.constant.JwtClaimsConstant;
import com.neko.dto.AdminLoginDTO;
import com.neko.entity.Admin;
import com.neko.properties.JwtProperties;
import com.neko.result.Result;
import com.neko.service.AdminService;
import com.neko.utils.JwtUtil;
import com.neko.vo.AdminLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private final AdminService adminService;
    private final JwtProperties jwtProperties;

    public AdminController(AdminService adminService, JwtProperties jwtProperties) {
        this.adminService = adminService;
        this.jwtProperties = jwtProperties;
    }

    @PostMapping("/login")
    public Result<AdminLoginVO> login(@RequestBody AdminLoginDTO adminLoginDTO) {
        log.info("User login: {}", adminLoginDTO);
        Admin admin = adminService.login(adminLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ADMIN_ID, admin.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        AdminLoginVO adminLoginVO = AdminLoginVO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .password(admin.getPassword())
                .token(token)
                .build();

        return Result.success(adminLoginVO);
    }

    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }
}
