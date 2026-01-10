package com.neko.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO implements Serializable {

    private Long id;

    private String username;

    private String email;

    private String phone;

    private Integer sex;

    private String avatar;

    private Integer status;
}
