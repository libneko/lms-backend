package com.neko.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowDetail implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    // 名称
    private String name;

    // 借阅记录 id
    private Long borrowRecordId;

    // 书本 id
    private Long bookId;

    // 数量
    private Integer number;

    // 图片
    private String image;
}
