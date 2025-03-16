package com.gill.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.NonNull;
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
public class MvcConfig {

    static class WebMvcConfig implements WebMvcConfigurer {

        private final List<HandlerInterceptor> interceptors;

        public WebMvcConfig(List<HandlerInterceptor> interceptors) {
            this.interceptors = interceptors;
        }

        @Override
        public void addInterceptors(@NonNull InterceptorRegistry registry) {
            for (HandlerInterceptor interceptor : interceptors) {
                registry.addInterceptor(interceptor).addPathPatterns("/**");
            }
        }

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            ObjectMapper objectMapper = converter.getObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addSerializer(Long.class, ToStringSerializer.instance);
            module.addSerializer(Long.TYPE, ToStringSerializer.instance);
            objectMapper.registerModule(module);
            converters.add(converter);
        }
    }


    @Bean
    public WebMvcConfigurer webMvcConfigurer(List<HandlerInterceptor> interceptors) {
        return new WebMvcConfig(interceptors);
    }
}
