package com.neko.service;

import com.neko.dto.AdminLoginDTO;
import com.neko.entity.Admin;

public interface AdminService {
    Admin login(AdminLoginDTO adminLoginDTO);
}
