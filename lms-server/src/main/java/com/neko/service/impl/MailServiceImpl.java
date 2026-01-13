package com.neko.service.impl;

import com.neko.service.MailService;
import com.neko.utils.CodeUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MailServiceImpl implements MailService {
    private final StringRedisTemplate stringRedisTemplate;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public MailServiceImpl(StringRedisTemplate stringRedisTemplate, JavaMailSender javaMailSender) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendCode(String email) {
        if (email == null || email.trim().isEmpty()) {
            log.error("邮箱地址为空，无法发送验证码");
            throw new IllegalArgumentException("邮箱地址不能为空");
        }

        // 邮箱格式验证
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            log.error("邮箱地址格式不正确: {}", email);
            throw new IllegalArgumentException("邮箱地址格式不正确");
        }

        String code = CodeUtil.generateCode(6);
        String key = "email:code:" + email;
        long timeout = 5;

        stringRedisTemplate.opsForValue().set(key, code, timeout, TimeUnit.MINUTES);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("图书馆");
        message.setText(code + "为您的验证码，请您于" + timeout + "分钟内填写。如非本人操作，请忽略本短信。");

        javaMailSender.send(message);
    }

    @Override
    public boolean verifyCode(String email, String code) {
        String key = "email:code:" + email;

        String redisCode = stringRedisTemplate.opsForValue().get(key);

        if (redisCode != null && redisCode.equals(code)) {
            // 验证成功后可以删除
            stringRedisTemplate.delete(key);
            return true;
        }
        return false;
    }
}
