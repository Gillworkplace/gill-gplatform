package com.gill.common.exception;

import lombok.Getter;

/**
 * BusinessException
 *
 * @author zhangzhiyan
 * @since 2025-03-11
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String message;

    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
