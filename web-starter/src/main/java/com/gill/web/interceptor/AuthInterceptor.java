package com.gill.web.interceptor;

import com.gill.api.domain.UserProperties;
import com.gill.api.service.user.IUserService;
import com.gill.web.annotation.IgnoreAuth;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedMethod;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * AuthInterceptor
 *
 * @author gill
 * @version 2024/02/13
 **/
@Slf4j
public abstract class AuthInterceptor implements HandlerInterceptor {

    protected abstract IUserService getUserService();

    @PostConstruct
    private void init() {
        log.info("load auth interceptor: {}", this.getClass().getCanonicalName());
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (handler instanceof AnnotatedMethod ha) {
            Method method = ha.getMethod();
            IgnoreAuth ignore = method.getAnnotation(IgnoreAuth.class);
            if (ignore != null) {
                return true;
            }
            Integer uid = Optional.ofNullable(
                    findCookie(request.getCookies(), UserProperties.USER_ID))
                .map(Cookie::getValue)
                .map(Integer::parseInt)
                .orElse(null);
            String token = Optional.ofNullable(
                    findCookie(request.getCookies(), UserProperties.TOKEN_ID))
                .map(Cookie::getValue)
                .orElse(null);
            getUserService().checkToken(uid, token);
            request.setAttribute(UserProperties.USER_ID, uid);
            request.setAttribute(UserProperties.TOKEN_ID, token);
        }
        return true;
    }

    private Cookie findCookie(Cookie[] cookies, String name) {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }
}
