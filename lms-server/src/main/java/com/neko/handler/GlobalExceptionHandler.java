package com.neko.handler;

import com.neko.constant.MessageConstant;
import com.neko.exception.BaseException;
import com.neko.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public Result<Object> exceptionHandler(BaseException ex) {
        log.error("Exception message: {}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result<Object> exceptionHandler(PSQLException ex) {
        ServerErrorMessage serverErrorMessage = ex.getServerErrorMessage();
        String detail = serverErrorMessage.getDetail();
        log.info("SQL Error: {}", ex.getMessage());
        if (serverErrorMessage.getSQLState().equals("23505")) {
            Pattern pattern = Pattern.compile("\\((.*?)\\)=\\((.*?)\\)");
            Matcher matcher = pattern.matcher(detail);
            if (matcher.find()) {
                String field = matcher.group(1);
                String value = matcher.group(2);
                if (field != null) {
                    switch (field) {
                        case "username":
                            return Result.error(MessageConstant.USERNAME_ALREADY_EXISTS);
                        case "email":
                            return Result.error(MessageConstant.EMAIL_ALREADY_EXISTS);
                        default:
                            String msg = String.format("%s %s", value, MessageConstant.ALREADY_EXISTS);
                            return Result.error(msg);
                    }
                }
                String msg = String.format("%s %s", value, MessageConstant.ALREADY_EXISTS);
                return Result.error(msg);
            }
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }
}
