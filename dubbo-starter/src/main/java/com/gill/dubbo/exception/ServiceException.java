package com.gill.dubbo.exception;

import lombok.Getter;

/**
 * ServiceException
 *
 * @author gill
 * @version 2024/02/24
 **/
@Getter
public class ServiceException extends RuntimeException {

    public static final String TYPE = "service-exception";

    public static final String CODE = "code";

    public static final String MESSAGE = "message";

    private final int code;

    private final String message;

    public ServiceException(int code) {
        this.code = code;
        this.message = "internal error";
    }

    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ServiceException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public ServiceException(Throwable cause) {
        super("internal error", cause);
        this.code = 500;
        this.message = "internal error";
    }
}
