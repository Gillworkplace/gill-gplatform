package com.gill.datasources;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * DatasourcesAutoConfiguration
 * <p>
 * 当spring.datasource.url为空时 多数据源需要在DataSourceAutoConfiguration前执行
 *
 * @author gill
 * @version 2023/12/18
 **/
@ConditionalOnExpression("#{T(com.google.common.base.Strings).isNullOrEmpty('${spring.datasource.url:}') && ${datasources.enabled:false}}")
@EnableConfigurationProperties({DataSources.class})
@ComponentScan(basePackages = "com.gill.datasources")
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@AutoConfiguration
@Import(DataSourcesConfiguration.class)
public class BeforeDataSourcesAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(
        BeforeDataSourcesAutoConfiguration.class);

    @PostConstruct
    private void init() {
        log.info("load dynamic datasources before DataSourcesAutoConfiguration");
    }
}
