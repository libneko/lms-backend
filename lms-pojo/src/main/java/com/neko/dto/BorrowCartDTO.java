package com.neko.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BorrowCartDTO implements Serializable {

    private Long bookId;

    private int number = 1;
}
