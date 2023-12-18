package com.gill.datasources;

import com.gill.datasources.config.DataSources;
import com.gill.datasources.config.DataSourceProperties;
import com.gill.datasources.generator.DataSourceGenerator;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;

/**
 * DatasourcesAutoConfiguration
 *
 * @author gill
 * @version 2023/12/18
 **/
@ConditionalOnProperty(prefix = "datasources", name = "enabled", havingValue = "true")
//@ConditionalOnExpression("false")
@EnableConfigurationProperties(DataSources.class)
@ComponentScan(basePackages = "com.gill.datasources")
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@AutoConfiguration
public class DataSourcesAutoConfiguration {

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private DataSources datasources;

    @Bean
    @Primary
    @ConditionalOnBean(DataSource.class)
    DataSource specificPrimaryDataSource(DataSource primaryDataSource) {
        Map<String, DataSourceProperties> sources = datasources.getSources();
        ConfigurableListableBeanFactory factory = context.getBeanFactory();
        for (Map.Entry<String, DataSourceProperties> entry : sources.entrySet()) {
            String name = entry.getKey();
            DataSourceProperties properties = entry.getValue();
            DataSource dataSource = DataSourceGenerator.chainsGenerate(properties);
            factory.registerSingleton(name, dataSource);
        }
        return primaryDataSource;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(DataSource.class)
    DataSource dynamicPrimaryDataSource() {
        Map<String, DataSourceProperties> sources = datasources.getSources();
        ConfigurableListableBeanFactory factory = context.getBeanFactory();
        DataSource first = null;
        DataSource primary = null;
        for (Map.Entry<String, DataSourceProperties> entry : sources.entrySet()) {
            String name = entry.getKey();
            DataSourceProperties properties = entry.getValue();
            DataSource dataSource = DataSourceGenerator.chainsGenerate(properties);
            if (first == null) {
                first = dataSource;
            }
            if (properties.isPrimary()) {
                primary = dataSource;
            }
            factory.registerSingleton(name, dataSource);
        }
        return primary == null ? first : primary;
    }
}
