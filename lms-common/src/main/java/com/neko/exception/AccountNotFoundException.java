package com.neko.exception;

public class AccountNotFoundException extends BaseException {

    public AccountNotFoundException(String msg) {
        super(msg);
    }
}