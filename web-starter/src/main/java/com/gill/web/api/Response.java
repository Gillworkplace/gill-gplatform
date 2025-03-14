package com.gill.web.api;

import cn.hutool.json.JSONUtil;
import com.gill.common.exception.BusinessCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

/**
 * Result
 *
 * @author gill
 * @version 2024/01/22
 **/
@Getter
public class Response<T> extends ResponseEntity<Object> {

    private static final String SUCCESS = "success";

    @Data
    @AllArgsConstructor
    public static class ResultWrapper<T> {

        private int code;

        private String message;

        private T data;
    }

    private Response(HttpStatus code, T data, HttpHeaders headers) {
        super(data, headers, code);
    }

    private Response(HttpStatus code, int businessCode, String message, T data,
        HttpHeaders headers) {
        super(new ResultWrapper<>(businessCode, message, data), headers, code);
    }

    @Override
    @NonNull
    public String toString() {
        StringBuilder builder = new StringBuilder("<");
        HttpStatusCode statusCode = getStatusCode();
        builder.append(statusCode);
        if (statusCode instanceof HttpStatus httpStatus) {
            builder.append(' ');
            builder.append(httpStatus.getReasonPhrase());
        }
        builder.append(',');
        Object body = getBody();
        HttpHeaders headers = getHeaders();
        if (body instanceof String str) {
            builder.append(str);
            builder.append(',');
        } else if (body instanceof InputStreamSource source) {
            builder.append("source,");
        } else if (body != null) {
            builder.append(JSONUtil.toJsonStr(body));
            builder.append(',');
        }
        builder.append(headers);
        builder.append('>');
        return builder.toString();
    }

    public static class ResponseBuilder<T> {

        private final HttpHeaders headers = new HttpHeaders();

        private final BusinessCode businessCode;

        private final HttpStatus code;

        private String message;

        private T data;

        private ResponseBuilder(BusinessCode businessCode, String message, T data) {
            this.businessCode = businessCode;
            this.code = HttpStatus.OK;
            this.message = message;
            this.data = data;
        }

        private ResponseBuilder(HttpStatus code, String message, T data) {
            this.businessCode = BusinessCode.IGNORE;
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
            return build(true);
        }

        public Response<T> build(boolean wrap) {
            if (wrap) {
                return new Response<>(code, businessCode.getCode(), message, data, headers);
            }
            return new Response<>(code, data, headers);
        }
    }


    public static ResponseBuilder<String> success() {
        return success("OK");
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> ResponseBuilder<T> success(T data) {
        return success(data, "OK");
    }

    /**
     * 成功返回结果
     *
     * @param data    获取的数据
     * @param message 提示信息
     */
    public static <T> ResponseBuilder<T> success(T data, String message) {
        return new ResponseBuilder<>(BusinessCode.SUCCESS, message, data);
    }

    /**
     * 失败返回结果
     */
    public static <T> ResponseBuilder<T> failed() {
        return failed(BusinessCode.SYSTEM_ERROR, "系统错误");
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> ResponseBuilder<T> failed(String message) {
        return failed(BusinessCode.BUSINESS_ERROR, message);
    }

    /**
     * 失败返回结果
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public static <T> ResponseBuilder<T> failed(BusinessCode code, String message) {
        return new ResponseBuilder<>(code, message, null);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> ResponseBuilder<T> validateFailed() {
        return failed(BusinessCode.BUSINESS_ERROR, "参数有误");
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> ResponseBuilder<T> validateFailed(String message) {
        return failed(BusinessCode.BUSINESS_ERROR, message);
    }

    /**
     * 未登录返回结果
     */
    public static <T> ResponseBuilder<T> unauthorized(String message) {
        return failed(BusinessCode.UNAUTHORIZED, message);
    }

    /**
     * 失败返回结果
     *
     * @param error 错误码
     */
    public static <T> ResponseBuilder<T> error(HttpStatus error) {
        return error(error, error.getReasonPhrase());
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> ResponseBuilder<T> error(String message) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    /**
     * 失败返回结果
     */
    public static <T> ResponseBuilder<T> error() {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    /**
     * 失败返回结果
     *
     * @param error   错误码
     * @param message 信息
     */
    public static <T> ResponseBuilder<T> error(HttpStatus error, String message) {
        return new ResponseBuilder<T>(error, message, null);
    }

    /**
     * 未授权返回结果
     */
    public static <T> Response<T> forbidden(T data) {
        return new ResponseBuilder<>(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase(),
            data).build(false);
    }
}
