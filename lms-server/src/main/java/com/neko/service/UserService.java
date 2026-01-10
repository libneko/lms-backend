package com.neko.service;

import com.neko.dto.UserCodeDTO;
import com.neko.dto.UserDTO;
import com.neko.dto.UserPageQueryDTO;
import com.neko.dto.UserPasswordDTO;
import com.neko.entity.User;
import com.neko.result.PageResult;
import com.neko.vo.UserVO;

import java.util.List;

public interface UserService {
    User register(UserPasswordDTO userPasswordDTO);

    User login(UserCodeDTO userCodeDTO);

    User login(UserPasswordDTO userPasswordDTO);

    UserVO getById(Long id);

    void update(UserDTO userDTO);

    PageResult<UserVO> pageQuery(UserPageQueryDTO userPageQueryDTO);

    void deleteBatch(List<Long> ids);

    void setStatus(Integer status, Long id);
}
