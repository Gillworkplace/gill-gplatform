package com.gill.web.interceptor;

import com.gill.api.domain.UserProperties;
import com.gill.web.annotation.IgnoreAuth;
import com.gill.web.exception.WebException;
import com.gill.web.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import org.springframework.core.annotation.AnnotatedMethod;
import org.springframework.http.HttpStatus;
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
            String csrfTokenCookie = RequestUtil.getParamFromRequestParamOrCookie(request,
                UserProperties.CSRF_TOKEN);

            // 接口需要用户凭证信息的情况下必须通过csrf校验
            // crsf 校验：header 和 cookie中的 csrf参数需要一样
            if (csrfTokenHeader == null || !csrfTokenHeader.equals(csrfTokenCookie)) {
                throw new WebException(HttpStatus.FORBIDDEN, "forbidden");
            }
        }
        return true;
    }
}
