package com.neko.enums;

import lombok.Getter;

@Getter
public enum Status {
    ENABLE(1),
    DISABLE(0);

    private final int code;

    Status(int code) {
        this.code = code;
    }

}