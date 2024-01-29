package com.gill.datasource;

import static com.gill.datasource.dynamic.DynamicDataSourceSelector.DEFAULT_DB;

import com.gill.datasource.dynamic.DynamicDataSource;
import com.gill.datasource.generator.DataSourceGenerator;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * DataSourcesAutoConfiguration
 * <p>
 * 当spring.datasource.url不为空时代表已经配置了数据源，此时多数据源需要在DataSourceAutoConfiguration后执行
 *
 * @author gill
 * @version 2023/12/18
 **/
@Configuration
public class DataSourcesConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DataSourcesConfiguration.class);

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private DataSources datasources;

    private record DataSourceInfo(DataSource defaultDataSource,
                                  Map<String, DataSource> dataSources) {

    }

    private DataSourceInfo generateDataSources() {
        Map<String, DataSourceProperties> sources = datasources.getSources();
        ConfigurableListableBeanFactory factory = context.getBeanFactory();
        Map<String, DataSource> dataSources = new HashMap<>();
        DataSource first = null;
        DataSource primary = null;
        for (Map.Entry<String, DataSourceProperties> entry : sources.entrySet()) {
            String name = entry.getKey();
            DataSourceProperties properties = entry.getValue();
            if (!properties.enabled()) {
                continue;
            }
            DataSource dataSource = DataSourceGenerator.chainsGenerate(datasources, properties);
            dataSources.put(name, dataSource);
            factory.registerSingleton(name, dataSource);
            if (first == null) {
                first = dataSource;
            }
            if (properties.isPrimary()) {
                primary = dataSource;
            }
            log.info("dynamic datasources load: {}", name);
        }
        return new DataSourceInfo(primary == null ? first : primary, dataSources);
    }

    @Primary
    @Bean
    @ConditionalOnBean(DataSource.class)
    public DataSource dynamicDataSourceOnBean(DataSource primary) {
        log.info("dynamicDataSourceOnBean: {}", primary);
        return dynamicDataSource(primary);
    }

    @Primary
    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dynamicDataSourceOnMissingBean() {
        log.info("dynamicDataSourceOnMissingBean");
        return dynamicDataSource(null);
    }

    private DataSource dynamicDataSource(DataSource primary) {
        DataSourceInfo dataSourceInfo = generateDataSources();
        Map<Object, Object> dataSources = new HashMap<>(dataSourceInfo.dataSources());
        primary = primary == null ? dataSourceInfo.defaultDataSource() : primary;
        context.getBeanFactory().registerSingleton(DEFAULT_DB, primary);
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setDefaultTargetDataSource(primary);
        dynamicDataSource.setTargetDataSources(dataSources);
        return dynamicDataSource;
    }
}
