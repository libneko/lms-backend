package com.neko.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable {

    private Long id;

    private String username;

    private String email;

    private String password;

    private String phone;

    private Integer sex;

    private String avatar;
}
