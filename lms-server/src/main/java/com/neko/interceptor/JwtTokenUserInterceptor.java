package com.neko.interceptor;

import com.neko.constant.JwtClaimsConstant;
import com.neko.context.BaseContext;
import com.neko.entity.User;
import com.neko.mapper.UserMapper;
import com.neko.properties.JwtProperties;
import com.neko.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;
    private final UserMapper userMapper;

    public JwtTokenUserInterceptor(JwtProperties jwtProperties, UserMapper userMapper) {
        this.jwtProperties = jwtProperties;
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String token = request.getHeader(jwtProperties.getUserTokenName());

        try {
            log.info("jwt validation: {}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long id = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("Current user id: {}", id);
            BaseContext.setCurrentId(id);

            // 检查用户状态
            User user = userMapper.getById(id);
            if (user == null) {
                log.warn("User not found: {}", id);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            // 检查用户是否被封号，0表示禁用
            if (user.getStatus() != null && user.getStatus() == 0) {
                log.warn("User account is banned: {}", id);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            return true;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}
