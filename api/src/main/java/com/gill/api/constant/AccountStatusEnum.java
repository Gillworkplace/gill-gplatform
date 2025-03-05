package com.gill.api.constant;

import lombok.Getter;

@Getter
public enum AccountStatusEnum {

    UNUSED(0),

    NORMAL(1),

    BANNED(2),

    DELETED(3);

    private final byte code;

    AccountStatusEnum(int code) {
        this.code = (byte) code;
    }
}
