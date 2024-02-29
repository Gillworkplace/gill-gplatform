package com.gill.web.interceptor;

import com.gill.api.domain.UserProperties;
import com.gill.web.annotation.IgnoreAuth;
import com.gill.web.util.RequestUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Optional;
import org.springframework.core.annotation.AnnotatedMethod;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * CSRFInterceptor
 *
 * @author gill
 * @version 2024/02/29
 **/
public class CsrfInterceptor implements HandlerInterceptor {

    private static final String CT = "Csrf-Token";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (handler instanceof AnnotatedMethod ha) {
            Method method = ha.getMethod();
            IgnoreAuth ignore = method.getAnnotation(IgnoreAuth.class);
            if (ignore != null) {
                return true;
            }
            String csrfTokenHeader = request.getHeader(CT);
            String csrfTokenCookie = Optional.ofNullable(
                    RequestUtil.findCookie(request.getCookies(), UserProperties.CSRF_TOKEN))
                .map(Cookie::getValue)
                .orElse(null);

            // 接口需要用户凭证信息的情况下必须通过csrf校验
            // crsf 校验：header 和 cookie中的 csrf参数需要一样
            if (csrfTokenHeader == null || csrfTokenCookie == null) {
                return false;
            }
            return csrfTokenHeader.equals(csrfTokenCookie);
        }
        return true;
    }
}
