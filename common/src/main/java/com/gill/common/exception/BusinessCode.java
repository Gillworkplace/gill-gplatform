package com.gill.common.exception;

import lombok.Getter;

/**
 * BusinessCode
 *
 * @author zhangzhiyan
 * @since 2025-03-14
 */
@Getter
public enum BusinessCode {

    IGNORE(-1),

    SUCCESS(0),

    BUSINESS_ERROR(400),

    UNAUTHORIZED(401),

    TOO_MANY_REQUEST(429),

    SYSTEM_ERROR(500);

    private final int code;

    BusinessCode(int code) {
        this.code = code;
    }

    public static BusinessCode getByCode(int code) {
        for (BusinessCode businessCode : BusinessCode.values()) {
            if (businessCode.getCode() == code) {
                return businessCode;
            }
        }
        return SYSTEM_ERROR;
    }
}
