package com.gill.web.api;

import lombok.Getter;
import lombok.ToString;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Result
 *
 * @author gill
 * @version 2024/01/22
 **/
@Getter
@ToString
public class Response<T> extends ResponseEntity<Object> {

    @Getter
    @ToString
    public static class ResultWrapper<T> {

        private final String message;

        private final T data;

        public ResultWrapper(String message, T data) {
            this.message = message;
            this.data = data;
        }
    }

    private Response(T data) {
        super(data, HttpStatus.OK);
    }

    private Response(HttpStatus code, String message, T data) {
        super(new ResultWrapper<>(message, data), code);
    }

    /**
     * 添加响应头
     *
     * @param key   key
     * @param value value
     * @return this
     */
    public Response<T> addHeader(String key, String value) {
        getHeaders().add(key, value);
        return this;
    }

    /**
     * 设置content-type
     *
     * @param mediaType content-type
     * @return this
     */
    public Response<T> contentType(MediaType mediaType) {
        getHeaders().setContentType(mediaType);
        return this;
    }

    public static Response<String> success() {
        return new Response<>(HttpStatus.OK, HttpStatus.OK.getReasonPhrase(), "");
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> Response<T> success(T data) {
        if (data instanceof InputStreamSource) {
            return new Response<>(data);
        }
        return new Response<>(HttpStatus.OK, HttpStatus.OK.getReasonPhrase(), data);
    }

    /**
     * 成功返回结果
     *
     * @param data    获取的数据
     * @param message 提示信息
     */
    public static <T> Response<T> success(T data, String message) {
        return new Response<>(HttpStatus.OK, message, data);
    }

    /**
     * 失败返回结果
     *
     * @param error 错误码
     */
    public static <T> Response<T> failed(HttpStatus error) {
        return new Response<>(error, error.getReasonPhrase(), null);
    }

    /**
     * 失败返回结果
     *
     * @param error   错误码
     * @param message 错误信息
     */
    public static <T> Response<T> failed(HttpStatus error, String message) {
        return new Response<>(error, message, null);
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> Response<T> failed(String message) {
        return new Response<T>(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> Response<T> failed() {
        return failed(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> Response<T> validateFailed() {
        return failed(HttpStatus.BAD_REQUEST);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> Response<T> validateFailed(String message) {
        return failed(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 未登录返回结果
     */
    public static <T> Response<T> unauthorized(T data) {
        return new Response<>(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.getReasonPhrase(),
            data);
    }

    /**
     * 未授权返回结果
     */
    public static <T> Response<T> forbidden(T data) {
        return new Response<>(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase(), data);
    }
}
