package com.gill.datasource;

import com.gill.datasource.dynamic.DynamicDataSourceSelector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import others.SpringbootApplication;
import others.mapper.TestMapper;

/**
 * MapperTests
 *
 * @author gill
 * @version 2023/12/19
 **/
@SpringBootTest(classes = SpringbootApplication.class, properties = "spring.config.location=classpath:application-both.yaml")
public class MapperWithDefaultDataSourceTests {

    @Autowired
    private TestMapper testMapper;

    @AfterEach
    public void after() {
        DynamicDataSourceSelector.remove();
    }

    @Test
    public void testUsePrimary() {
        Assertions.assertEquals("TEST0", testMapper.queryDbName());
    }

    @Test
    public void testUseDefault() {
        DynamicDataSourceSelector.use(DynamicDataSourceSelector.DEFAULT_DB);
        Assertions.assertEquals("TEST0", testMapper.queryDbName());
        DynamicDataSourceSelector.use("db");
        Assertions.assertEquals("TEST1", testMapper.queryDbName());
    }

    @Test
    public void testSelectDataSource() {
        DynamicDataSourceSelector.use("db-druid");
        Assertions.assertEquals("TEST2", testMapper.queryDbName());
        DynamicDataSourceSelector.use("db-hikari");
        Assertions.assertEquals("TEST3", testMapper.queryDbName());
    }
}
