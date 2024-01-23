package com.gill.web.api;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

/**
 * Result
 *
 * @author gill
 * @version 2024/01/22
 **/
@Getter
@ToString
public class Result<T> {

    /**
     * 状态码
     */
    private final long code;
    /**
     * 提示信息
     */
    private final String message;
    /**
     * 数据封装
     */
    private final T data;

    private Result(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result<String> success() {
        return new Result<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "");
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), data);
    }

    /**
     * 成功返回结果
     *
     * @param data    获取的数据
     * @param message 提示信息
     */
    public static <T> Result<T> success(T data, String message) {
        return new Result<>(HttpStatus.OK.value(), message, data);
    }

    /**
     * 失败返回结果
     *
     * @param error 错误码
     */
    public static <T> Result<T> failed(HttpStatus error) {
        return new Result<>(error.value(), error.getReasonPhrase(), null);
    }

    /**
     * 失败返回结果
     *
     * @param error   错误码
     * @param message 错误信息
     */
    public static <T> Result<T> failed(HttpStatus error, String message) {
        return new Result<T>(error.value(), message, null);
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> Result<T> failed(String message) {
        return new Result<T>(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> Result<T> failed() {
        return failed(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> Result<T> validateFailed() {
        return failed(HttpStatus.BAD_REQUEST);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> Result<T> validateFailed(String message) {
        return failed(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 未登录返回结果
     */
    public static <T> Result<T> unauthorized(T data) {
        return new Result<>(HttpStatus.UNAUTHORIZED.value(),
            HttpStatus.UNAUTHORIZED.getReasonPhrase(), data);
    }

    /**
     * 未授权返回结果
     */
    public static <T> Result<T> forbidden(T data) {
        return new Result<>(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
            data);
    }
}
