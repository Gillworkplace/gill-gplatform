package com.gill.datasources;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import others.SpringbootApplication;

@SpringBootTest(classes = SpringbootApplication.class, properties = "spring.config.location=classpath:application-unable.yaml")
class DataSourcesUnableConfigTests {

    @Autowired
    private ApplicationContext context;

    @Test
    public void testConfig() {
        Assertions.assertThrows(NoSuchBeanDefinitionException.class,
            () -> context.getBean("datasources", DataSources.class));
    }
}
