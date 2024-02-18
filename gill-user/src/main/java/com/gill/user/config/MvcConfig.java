package com.gill.user.config;

import com.gill.user.interceptor.AuthInterceptor;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludes = List.of("/login", "/register", "/precheck/username",
            "/invite/login");
        registry.addInterceptor(authInterceptor).excludePathPatterns(excludes);
    }
}
