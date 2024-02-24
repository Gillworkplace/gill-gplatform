package com.gill.web.exception;

import com.gill.dubbo.exception.ServiceException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * WebException
 *
 * @author gill
 * @version 2024/01/22
 **/
@Getter
public class WebException extends ServiceException {

    public static final WebException DEFAULT_WEB_EXCEPTION = new WebException(
        HttpStatus.BAD_REQUEST);

    private final HttpStatus status;

    public WebException(HttpStatus status) {
        super(status.value());
        this.status = status;
    }

    public WebException(HttpStatus status, String message) {
        super(status.value(), message);
        this.status = status;
    }

    public WebException(ServiceException ex) {
        super(ex.getCode(), ex.getMessage(), ex.getCause());
        HttpStatus httpCode = HttpStatus.resolve(ex.getCode());
        this.status = httpCode == null ? HttpStatus.INTERNAL_SERVER_ERROR : httpCode;
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

    /**
     * 是否为未授权异常
     *
     * @return 是否
     */
    public boolean isUnauthorized() {
        return this.status == HttpStatus.UNAUTHORIZED;
    }
}
