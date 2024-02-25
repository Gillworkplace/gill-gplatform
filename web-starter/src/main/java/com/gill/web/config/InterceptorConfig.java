package com.gill.web.config;

import com.gill.web.interceptor.AuthInterceptor;
import com.gill.web.interceptor.PermissionInterceptor;
import com.gill.web.interceptor.RemoteAuthInterceptor;
import com.gill.web.interceptor.RemotePermissionInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * InterceptorConfig
 *
 * @author gill
 * @version 2024/02/25
 **/
@Slf4j
@Configuration
public class InterceptorConfig {

    @ConditionalOnMissingBean(AuthInterceptor.class)
    @Bean
    public AuthInterceptor remoteAuthInterceptor() {
        return new RemoteAuthInterceptor();
    }

    @ConditionalOnMissingBean(PermissionInterceptor.class)
    @Bean
    public PermissionInterceptor remotePermissionInterceptor() {
        return new RemotePermissionInterceptor();
    }
}
