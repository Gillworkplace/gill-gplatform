package com.gill.datasource;

import com.gill.datasource.dynamic.DynamicDataSource;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import others.SpringbootApplication;

@SpringBootTest(classes = SpringbootApplication.class, properties = "spring.config.location=classpath:application-multiprimary.yaml")
class DataSourcesMultiPrimaryConfigTests {

    @Autowired
    private ApplicationContext context;

    @Test
    public void testConfig() {
        Assertions.assertInstanceOf(DynamicDataSource.class, context.getBean(DataSource.class));
    }
}
