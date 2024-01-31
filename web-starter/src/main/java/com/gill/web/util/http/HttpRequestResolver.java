package com.gill.web.util.http;

import jakarta.servlet.http.HttpServletRequest;

/**
 * HttpRequestParser
 *
 * @author gill
 * @version 2024/01/31
 **/
public interface HttpRequestResolver {

    /**
     * 从HttpRequest中获取指定的参数
     *
     * @param request request
     * @param key     参数名
     * @return 参数值
     */
    String get(HttpServletRequest request, String key);

}
