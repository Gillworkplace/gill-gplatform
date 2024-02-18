package com.gill.web.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * WebException
 *
 * @author gill
 * @version 2024/01/22
 **/
@Getter
public class WebException extends RuntimeException {

    public static final WebException DEFAULT_WEB_EXCEPTION = new WebException(
        HttpStatus.BAD_REQUEST);

    private final HttpStatus status;

    public WebException(HttpStatus status) {
        this.status = status;
    }

    public WebException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public WebException(Throwable cause) {
        super(cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * 是否为系统异常
     *
     * @return 是否
     */
    public boolean isInternalException() {
        return this.status == HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
