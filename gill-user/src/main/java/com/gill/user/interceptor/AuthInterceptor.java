package com.gill.user.interceptor;

import com.gill.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * AuthInterceptor
 *
 * @author gill
 * @version 2024/02/13
 **/
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response, @NonNull Object handler) {
        Integer uid = Optional.ofNullable(findCookie(request.getCookies(), "uid"))
            .map(Cookie::getValue)
            .map(Integer::parseInt)
            .orElse(null);
        String token = Optional.ofNullable(findCookie(request.getCookies(), "token"))
            .map(Cookie::getValue)
            .orElse(null);
        userService.checkToken(uid, token);
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
