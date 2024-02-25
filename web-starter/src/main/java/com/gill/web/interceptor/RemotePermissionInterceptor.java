package com.gill.web.interceptor;

import com.gill.api.service.user.IUserService;
import com.gill.dubbo.contant.Filters;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * RemoteAuthInterceptor
 *
 * @author gill
 * @version 2024/02/24
 **/
public class RemotePermissionInterceptor extends PermissionInterceptor{

    @DubboReference(filter = Filters.CONSUMER, check = false, lazy = true)
    private IUserService userService;

    @Override
    protected IUserService getUserService() {
        return userService;
    }
}
