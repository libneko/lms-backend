package com.neko.exception;

/**
 * 用户已存在异常
 */
public class UserAlreadyExistsException extends BaseException {

    public UserAlreadyExistsException() {
    }

    public UserAlreadyExistsException(String msg) {
        super(msg);
    }

}
