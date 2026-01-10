package com.neko.aspect;

import com.neko.annotation.AutoFill;
import com.neko.enums.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    @Pointcut("execution(* com.neko.mapper.*.*(..)) && @annotation(com.neko.annotation.AutoFill)")
    public void autoFillPointcut() {
    }

    @Before("autoFillPointcut()")
    public void beforeAutoFill(JoinPoint joinPoint) {
        log.info("before AutoFillAspect");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType type = autoFill.value();
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }

        Object entity = args[0];

        LocalDateTime now = LocalDateTime.now();

        if (type == OperationType.INSERT) {
            try {
                entity.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class).invoke(entity, now);
                entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class).invoke(entity, now);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (type == OperationType.UPDATE) {
            try {
                entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class).invoke(entity, now);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
