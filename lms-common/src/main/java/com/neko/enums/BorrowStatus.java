package com.neko.enums;

import lombok.Getter;

@Getter
public enum BorrowStatus {
    BORROWED(1), // 已借出
    RETURNED(2), // 已归还
    OVERDUE(3); // 已逾期

    private final int code;

    BorrowStatus(int code) {
        this.code = code;
    }
}
