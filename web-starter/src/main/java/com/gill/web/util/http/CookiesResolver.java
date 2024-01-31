package com.gill.web.util.http;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * CookiesParser
 *
 * @author gill
 * @version 2024/01/31
 **/
public class CookiesResolver implements HttpRequestResolver {

    /**
     * 从HttpRequest中获取指定的参数
     *
     * @param request request
     * @param key     参数名
     * @return 参数值
     */
    @Override
    public String get(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String value = cookie.getAttribute(key);
                if (StrUtil.isNotBlank(value)) {
                    return value;
                }
            }
        }
        return "";
    }
}
