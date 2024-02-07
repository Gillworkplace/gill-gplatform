package com.gill.web.api;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpHeaders;
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

    private Response(HttpStatus code, String message, T data, HttpHeaders headers) {
        super(new ResultWrapper<>(message, data), headers, code);
    }

    public static class ResponseBuilder<T> {

        private final HttpHeaders headers = new HttpHeaders();

        private HttpStatus code = HttpStatus.OK;

        private String message;

        private T data;

        private ResponseBuilder(T data) {
            this.data = data;
        }

        private ResponseBuilder(HttpStatus code, String message, T data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }


        /**
         * 设置请求头
         *
         * @param key   key
         * @param value value
         * @return this
         */
        public ResponseBuilder<T> addHeader(String key, String value) {
            headers.add(key, value);
            return this;
        }

        /**
         * 设置content-type
         *
         * @param mediaType content-type
         * @return this
         */
        public ResponseBuilder<T> contentType(MediaType mediaType) {
            headers.setContentType(mediaType);
            return this;
        }

        /**
         * 设置响应消息
         *
         * @param message 响应消息
         * @return this
         */
        public ResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        /**
         * 设置数据
         *
         * @param data content-type
         * @return this
         */
        public ResponseBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Response<T> build() {
            return new Response<>(code, message, data, headers);
        }
    }


    public static ResponseBuilder<String> success() {
        return new ResponseBuilder<>(HttpStatus.OK, HttpStatus.OK.getReasonPhrase(), "");
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> ResponseBuilder<T> success(T data) {
        return new ResponseBuilder<>(data);
    }

    /**
     * 成功返回结果
     *
     * @param data    获取的数据
     * @param message 提示信息
     */
    public static <T> ResponseBuilder<T> success(T data, String message) {
        return new ResponseBuilder<>(HttpStatus.OK, message, data);
    }

    /**
     * 失败返回结果
     *
     * @param error 错误码
     */
    public static ResponseBuilder<?> failed(HttpStatus error) {
        return new ResponseBuilder<>(error, error.getReasonPhrase(), null);
    }

    /**
     * 失败返回结果
     *
     * @param error   错误码
     * @param message 错误信息
     */
    public static ResponseBuilder<?> failed(HttpStatus error, String message) {
        return new ResponseBuilder<>(error, message, null);
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static ResponseBuilder<?> failed(String message) {
        return new ResponseBuilder<>(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
    }

    /**
     * 失败返回结果
     */
    public static ResponseBuilder<?> failed() {
        return failed(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 参数验证失败返回结果
     */
    public static ResponseBuilder<?> validateFailed() {
        return failed(HttpStatus.BAD_REQUEST);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     */
    public static ResponseBuilder<?> validateFailed(String message) {
        return failed(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 未登录返回结果
     */
    public static <T> ResponseBuilder<T> unauthorized(T data) {
        return new ResponseBuilder<>(HttpStatus.UNAUTHORIZED,
            HttpStatus.UNAUTHORIZED.getReasonPhrase(), data);
    }

    /**
     * 未授权返回结果
     */
    public static <T> ResponseBuilder<T> forbidden(T data) {
        return new ResponseBuilder<>(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase(),
            data);
    }
}
