package com.gill.datasources;

import com.gill.datasources.dynamic.DynamicDataSourceSelector;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import others.SpringbootApplication;
import others.mapper.TestMapper;

/**
 * MapperTests
 *
 * @author gill
 * @version 2023/12/19
 **/
@SpringBootTest(classes = SpringbootApplication.class, properties = "spring.config.location=classpath:application-only-datasource.yaml")
public class MapperWithoutDynamicDataSourceTests {

    @Autowired
    private ApplicationContext context;

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
    }

    @Test
    public void testFindDynamicDataSource_shouldCount0() {
        Assertions.assertThrows(NoSuchBeanDefinitionException.class,
            () -> context.getBean(DataSources.class));
    }

    @Test
    public void testDataSource_shouldCount1() {
        Map<String, DataSource> dataSources = context.getBeansOfType(DataSource.class);
        Assertions.assertEquals(1, dataSources.size());
    }
}
