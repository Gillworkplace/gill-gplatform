package com.gill.datasources;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import others.SpringbootApplication;

@SpringBootTest(classes = SpringbootApplication.class, properties = "spring.config.location=classpath:application-multiprimary.yaml")
class DataSourcesMultiPrimaryConfigTests {

    @Autowired
    private ApplicationContext context;

    @Test
    public void testConfig() {
        Assertions.assertInstanceOf(HikariDataSource.class, context.getBean(DataSource.class));
    }
}
