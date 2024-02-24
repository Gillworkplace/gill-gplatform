package com.gill.user.interceptor;

import com.gill.api.service.user.IUserService;
import com.gill.user.service.UserService;
import com.gill.web.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * PermissionInterceptor
 *
 * @author gill
 * @version 2024/02/18
 **/
@Component
public class LocalPermissionInterceptor extends PermissionInterceptor {

    @Autowired
    private UserService userService;

    @Override
    protected IUserService getUserService() {
        return userService;
    }
}
