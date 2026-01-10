package com.neko.config;

import com.neko.interceptor.JwtTokenAdminInterceptor;
import com.neko.interceptor.JwtTokenUserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final JwtTokenAdminInterceptor jwtTokenAdminInterceptor;
    private final JwtTokenUserInterceptor jwtTokenUserInterceptor;

    public WebMvcConfiguration(JwtTokenAdminInterceptor jwtTokenAdminInterceptor, JwtTokenUserInterceptor jwtTokenUserInterceptor) {
        this.jwtTokenAdminInterceptor = jwtTokenAdminInterceptor;
        this.jwtTokenUserInterceptor = jwtTokenUserInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("Register custom interceptor...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login");

        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/login/**")
                .excludePathPatterns("/user/register/**")
                .excludePathPatterns("/user/book/**")
                .excludePathPatterns("/user/category/list");
    }
}
