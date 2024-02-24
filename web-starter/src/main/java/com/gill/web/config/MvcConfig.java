package com.gill.web.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MvcConfig
 *
 * @author gill
 * @version 2024/02/18
 **/
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private List<HandlerInterceptor> interceptors = new ArrayList<>();

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        for (HandlerInterceptor interceptor : interceptors) {
            registry.addInterceptor(interceptor).addPathPatterns("/**");
        }
    }
}
