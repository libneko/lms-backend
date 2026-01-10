package com.neko.service;

public interface MailService {
    void sendCode(String email);

    boolean verifyCode(String email, String code);
}
