package com.neko.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowSubmitVO implements Serializable {
    // 借阅记录 id
    private Long id;
    // 借阅号
    private String borrowNumber;
    // 借阅时间
    private LocalDateTime borrowTime;
}