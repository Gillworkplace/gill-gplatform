package com.gill.web.interceptor;

import com.gill.api.domain.UserProperties;
import com.gill.api.service.user.IUserService;
import com.gill.web.annotation.IgnoreAuth;
import com.gill.web.annotation.OperationPermission;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedMethod;
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

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (handler instanceof AnnotatedMethod ha) {
            Method method = ha.getMethod();
            IgnoreAuth ignore = method.getAnnotation(IgnoreAuth.class);
            if (ignore == null) {
                return true;
            }
            Integer uid = (Integer) request.getAttribute(UserProperties.USER_ID);
            OperationPermission permission = method.getAnnotation(OperationPermission.class);
            if (permission == null) {
                return true;
            }
            getUserService().checkPermission(uid, permission.permissionExpression(),
                permission.exceptionCode(), permission.exceptionMessage());
        }
        return true;
    }
}
