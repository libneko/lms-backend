package com.neko.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.neko.constant.MessageConstant;
import com.neko.dto.UserCodeDTO;
import com.neko.dto.UserDTO;
import com.neko.dto.UserPageQueryDTO;
import com.neko.dto.UserPasswordDTO;
import com.neko.entity.User;
import com.neko.enums.Status;
import com.neko.exception.AccountLockedException;
import com.neko.exception.AccountNotFoundException;
import com.neko.exception.DeletionNotAllowedException;
import com.neko.exception.PasswordErrorException;
import com.neko.mapper.UserMapper;
import com.neko.result.PageResult;
import com.neko.service.MailService;
import com.neko.service.UserService;
import com.neko.utils.CodeUtil;
import com.neko.utils.PasswordUtil;
import com.neko.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final String avatar = "https://neko-book.oss-cn-hangzhou.aliyuncs.com/default_avatar.jpg";
    private final MailService mailService;

    public UserServiceImpl(UserMapper userMapper, MailService mailService) {
        this.userMapper = userMapper;
        this.mailService = mailService;
    }

    @Override
    public User register(UserPasswordDTO userPasswordDTO) {
        String email = userPasswordDTO.getEmail();
        String code = userPasswordDTO.getCode();

        if (!mailService.verifyCode(email, code)) {
            throw new AccountLockedException(MessageConstant.VERIFY_CODE_ERROR);
        }

        User user = new User();
        BeanUtils.copyProperties(userPasswordDTO, user);

        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        user.setAvatar(avatar);
        user.setStatus(Status.ENABLE.getCode());

        userMapper.insert(user);
        return user;
    }

    @Override
    @Transactional
    public User login(UserCodeDTO userCodeDTO) {
        String email = userCodeDTO.getEmail();
        String code = userCodeDTO.getCode();

        if (!mailService.verifyCode(email, code)) {
            throw new AccountLockedException(MessageConstant.VERIFY_CODE_ERROR);
        }

        User user = userMapper.getByEmail(email);

        if (user == null) {
            // 用户不存在，自动注册用户
            user = new User();
            user.setUsername("小书架用户_" + CodeUtil.generate(8));
            user.setEmail(email);
            user.setAvatar(avatar);
            user.setStatus(Status.ENABLE.getCode());
            userMapper.insert(user);
        }

        if (user.getStatus().equals(Status.DISABLE.getCode())) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        user = userMapper.getByEmail(email);

        return user;
    }

    @Override
    public User login(UserPasswordDTO userPasswordDTO) {
        String email = userPasswordDTO.getEmail();
        String password = userPasswordDTO.getPassword();

        User user = userMapper.getByEmail(email);

        if (user == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        if (!PasswordUtil.checkPassword(password, user.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (user.getStatus().equals(Status.DISABLE.getCode())) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        return user;
    }

    @Override
    public UserVO getById(Long id) {
        User user = userMapper.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public void update(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        userMapper.update(user);
    }

    @Override
    public PageResult<UserVO> pageQuery(UserPageQueryDTO userPageQueryDTO) {
        PageHelper.startPage(userPageQueryDTO.getPage(), userPageQueryDTO.getPageSize());
        Page<User> page = userMapper.pageQuery(userPageQueryDTO);
        List<UserVO> list = page.getResult().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).toList();
        return new PageResult<>(page.getTotal(), list);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        List<User> users = userMapper.getByIds(ids);
        if (users.stream().anyMatch(user -> Objects.equals(user.getStatus(), Status.ENABLE.getCode()))) {
            throw new DeletionNotAllowedException(MessageConstant.USER_IS_ACTIVE);
        }

        userMapper.deleteByIds(ids);
    }

    @Override
    public void setStatus(Integer status, Long id) {
        User user = User.builder()
                .id(id)
                .status(status)
                .build();
        userMapper.update(user);
    }
}
