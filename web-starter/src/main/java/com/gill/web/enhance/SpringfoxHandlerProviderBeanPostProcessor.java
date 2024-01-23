package com.gill.web.enhance;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;

/**
 * SpringfoxBeanPostProcessor
 *
 * @author gill
 * @version 2024/01/22
 **/
//@Component
public class SpringfoxHandlerProviderBeanPostProcessor implements BeanPostProcessor {


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
        throws BeansException {
        if (bean instanceof WebMvcRequestHandlerProvider
            || bean instanceof WebFluxRequestHandlerProvider) {
            customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
        }
        return bean;
    }

    private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(
        List<T> mappings) {
        List<T> copy = mappings.stream()
            .filter(mapping -> mapping.getPatternParser() == null)
            .toList();
        mappings.clear();
        mappings.addAll(copy);
    }

    @SuppressWarnings("unchecked")
    private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
        try {
            Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
            if (field != null) {
                field.setAccessible(true);
                return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
            }
            return Collections.emptyList();
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
