package com.gill.web.interceptor;

import com.gill.api.domain.UserProperties;
import com.gill.api.service.user.IUserService;
import com.gill.common.threadlocal.ThreadLocals;
import com.gill.web.annotation.IgnoreAuth;
import com.gill.web.exception.WebException;
import com.gill.web.util.RequestUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedMethod;
import org.springframework.http.HttpStatus;
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
            Long uid = Optional.ofNullable(
                    RequestUtil.getParamFromRequestParamOrCookie(request, UserProperties.USER_ID))
                .map(Long::parseLong)
                .orElse(null);
            String token = RequestUtil.getParamFromRequestParamOrCookie(request,
                UserProperties.TOKEN_ID);
            if (uid == null || token == null) {
                throw new WebException(HttpStatus.UNAUTHORIZED, "unauthorized");
            }
            getUserService().checkToken(uid, token);

            ThreadLocals.USER_ID.set(uid);
            ThreadLocals.TOKEN.set(token);

            request.setAttribute(UserProperties.USER_ID, uid);
            request.setAttribute(UserProperties.TOKEN_ID, token);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
        Object handler, Exception ex) throws Exception {
        ThreadLocals.USER_ID.remove();
        ThreadLocals.TOKEN.remove();
    }
}
