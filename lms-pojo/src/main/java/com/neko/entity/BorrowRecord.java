package com.neko.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    // 借阅号
    private String number;

    // 借阅状态
    private Integer status;

    // 借阅用户 id
    private Long userId;

    // 借阅时间
    private LocalDateTime borrowTime;

    // 用户名
    private String userName;

    // 归还时间
    private LocalDateTime returnTime;

    // 到期时间
    private LocalDateTime dueDate;

    // 续借次数
    private Integer renewCount;
}