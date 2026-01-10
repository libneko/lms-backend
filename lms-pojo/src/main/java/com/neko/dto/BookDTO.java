package com.neko.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BookDTO implements Serializable {

    private Long id;

    private String name;

    private String author;

    private Long categoryId;

    private BigDecimal price;

    private String image;

    private String description;

    private Integer status;

    private Long stock;

    private String isbn;

    private String location;

    private String publisher;
}
