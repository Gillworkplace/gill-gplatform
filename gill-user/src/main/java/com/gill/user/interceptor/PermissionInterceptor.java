package com.gill.user.interceptor;

import com.gill.api.domain.UserProperties;
import com.gill.user.service.UserService;
import com.gill.web.annotation.IgnoreAuth;
import com.gill.web.annotation.OperationPermission;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * PermissionInterceptor
 *
 * @author gill
 * @version 2024/02/18
 **/
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

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
            userService.checkPermission(uid, permission.permissionExpression(),
                permission.exceptionCode(), permission.exceptionMessage());
        }
        return true;
    }
}
