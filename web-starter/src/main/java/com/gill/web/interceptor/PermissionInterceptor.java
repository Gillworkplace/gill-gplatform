package com.gill.web.interceptor;

import com.gill.api.domain.UserProperties;
import com.gill.api.service.user.IUserService;
import com.gill.common.exception.BusinessException;
import com.gill.web.annotation.IgnoreAuth;
import com.gill.web.annotation.OperationPermission;
import com.gill.web.exception.WebException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * PermissionInterceptor
 *
 * @author gill
 * @version 2024/02/18
 **/
@Slf4j
public abstract class PermissionInterceptor implements HandlerInterceptor {

    protected abstract IUserService getUserService();

    @PostConstruct
    private void init() {
        log.info("load permission interceptor: {}", this.getClass().getCanonicalName());
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
            Long uid = (Long) request.getAttribute(UserProperties.USER_ID);
            OperationPermission permission = method.getAnnotation(OperationPermission.class);
            if (permission == null) {
                return true;
            }
            try {
                getUserService().checkPermission(uid, permission.permissionExpression(),
                    permission.exceptionCode(), permission.exceptionMessage());
            } catch (BusinessException ex) {
                throw new WebException(HttpStatus.FORBIDDEN, "forbidden");
            }

        }
        return true;
    }
}
