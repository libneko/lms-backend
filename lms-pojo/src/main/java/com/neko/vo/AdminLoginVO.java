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
public class AdminLoginVO implements Serializable {

    private Long id;

    private String username;

    private String password;

    private String token;
}
