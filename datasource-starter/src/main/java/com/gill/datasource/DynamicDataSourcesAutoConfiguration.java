package com.gill.datasource;

import com.gill.datasource.dynamic.DynamicDataSource;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * DataSourcesAutoConfiguration
 * <p>
 *
 * @author gill
 * @version 2023/12/18
 **/
@EnableConfigurationProperties({DataSources.class})
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@AutoConfigureBefore(MybatisConfiguration.class)
@Import({MybatisConfiguration.class, DataSourcesConfiguration.class})
public class DynamicDataSourcesAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(
        DynamicDataSourcesAutoConfiguration.class);

    @PostConstruct
    private void init() {
        log.info("load dynamic datasources");
    }
}
