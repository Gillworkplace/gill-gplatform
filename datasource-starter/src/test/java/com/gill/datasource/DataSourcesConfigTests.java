package com.gill.datasource;

import com.gill.common.decryption.DecryptionFactory;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import others.SpringbootApplication;

@SpringBootTest(classes = SpringbootApplication.class, properties = "spring.config.location=classpath:application.yaml")
class DataSourcesConfigTests {

    @Autowired
    private ApplicationContext context;

    @Test
    public void testConfig() {
        DataSources datasources = context.getBean("datasources", DataSources.class);
        Assertions.assertNotNull(datasources);
        Map<String, DataSourceProperties> map = datasources.getSources();
        Assertions.assertFalse(map.isEmpty());
        DataSourceProperties db = map.get("db");
        Assertions.assertNotNull(db);
        Assertions.assertEquals("jdbc:h2:mem:test", db.url());
        Assertions.assertEquals("sa", db.username());
        Assertions.assertEquals("", db.password());
        Assertions.assertEquals("org.h2.Driver", db.driverClassName());
    }

    @Test
    public void testDecryptionStrategySPI() {
        Assertions.assertNotNull(DecryptionFactory.getStrategy("mock"));
    }

    @Test
    public void testGetDataSource() {
        Assertions.assertNotNull(context.getBean("db", DataSource.class));
        Assertions.assertNotNull(context.getBean("db-druid", DataSource.class));
        Assertions.assertNotNull(context.getBean("db-hikari", DataSource.class));
    }
}
