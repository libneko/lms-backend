package com.neko.mapper;

import com.github.pagehelper.Page;
import com.neko.annotation.AutoFill;
import com.neko.dto.UserPageQueryDTO;
import com.neko.entity.User;
import com.neko.enums.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from users where email = #{email}")
    User getByEmail(String email);

    @Insert("insert into users (username, email, password, phone, sex, avatar, create_time, update_time, status)"
            +
            "values " +
            "(#{username}, #{email}, #{password}, #{phone}, #{sex}, #{avatar}, #{createTime}, #{updateTime}, #{status})")
    @AutoFill(value = OperationType.INSERT)
    void insert(User user);

    @Select("select * from users where id = #{id}")
    User getById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void update(User user);

    Page<User> pageQuery(UserPageQueryDTO userPageQueryDTO);

    List<User> getByIds(List<Long> ids);

    void deleteByIds(List<Long> ids);
}