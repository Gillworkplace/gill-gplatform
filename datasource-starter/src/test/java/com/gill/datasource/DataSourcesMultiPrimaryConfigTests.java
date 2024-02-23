package com.gill.datasource;

import cn.hutool.core.util.ReflectUtil;
import com.gill.datasource.dynamic.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;
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
        DataSource datasource = context.getBean(DataSource.class);
        Assertions.assertInstanceOf(DynamicDataSource.class, datasource);
        DynamicDataSource dds = (DynamicDataSource) datasource;
        Object defaultDatasource = ReflectUtil.getFieldValue(dds, "defaultTargetDataSource");
        Assertions.assertInstanceOf(HikariDataSource.class, defaultDatasource);
    }
}
