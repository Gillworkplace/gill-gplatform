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

    public static final String EXCEPTION_TYPE = "business-exception";

    private final BusinessCode code;

    private final String message;

    public BusinessException(String message) {
        super(message);
        this.code = BusinessCode.BUSINESS_ERROR;
        this.message = message;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = BusinessCode.BUSINESS_ERROR;
        this.message = message;
    }

    public BusinessException(BusinessCode code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(BusinessCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}
