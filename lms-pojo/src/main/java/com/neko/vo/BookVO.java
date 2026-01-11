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
public class BookVO implements Serializable {

    private Long id;

    private String name;

    private String author;

    private Long categoryId;

    private String image;

    private String description;

    private Integer status;

    private LocalDateTime updateTime;

    private Long stock;

    private String isbn;

    private String location;

    private String publisher;
}