package com.gill.user.interceptor;

import com.gill.api.service.user.IUserService;
import com.gill.user.service.UserService;
import com.gill.web.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AuthInterceptor
 *
 * @author gill
 * @version 2024/02/13
 **/
@Component
public class LocalAuthInterceptor extends AuthInterceptor {

    @Autowired
    private UserService userService;

    @Override
    protected IUserService getUserService() {
        return userService;
    }
}
