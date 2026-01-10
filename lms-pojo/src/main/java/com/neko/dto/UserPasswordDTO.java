package com.neko.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPasswordDTO implements Serializable {
    private String username;

    private String email;

    private String password;

    private String code;
}