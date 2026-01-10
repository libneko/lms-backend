package com.neko.service.impl;

import com.neko.constant.MessageConstant;
import com.neko.dto.AdminLoginDTO;
import com.neko.entity.Admin;
import com.neko.exception.AccountNotFoundException;
import com.neko.exception.PasswordErrorException;
import com.neko.mapper.AdminMapper;
import com.neko.service.AdminService;
import com.neko.utils.PasswordUtil;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    private final AdminMapper adminMapper;

    public AdminServiceImpl(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    @Override
    public Admin login(AdminLoginDTO adminLoginDTO) {
        String username = adminLoginDTO.getUsername();
        String password = adminLoginDTO.getPassword();

        Admin admin = adminMapper.getByUsername(username);

        if (admin == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        if (!PasswordUtil.checkPassword(password, admin.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        return admin;
    }
}
