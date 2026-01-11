package com.neko.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BorrowSubmitDTO implements Serializable {

    /**
     * 要借阅的图书ID列表
     */
    private List<Long> bookIds;
}
