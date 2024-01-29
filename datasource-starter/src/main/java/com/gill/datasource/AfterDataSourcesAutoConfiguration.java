package com.gill.datasource;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * DataSourcesAutoConfiguration
 * <p>
 * 当spring.datasource.url不为空时代表已经配置了数据源，此时多数据源需要在DataSourceAutoConfiguration后执行
 *
 * @author gill
 * @version 2023/12/18
 **/
@ConditionalOnExpression("#{!T(com.google.common.base.Strings).isNullOrEmpty('${spring.datasource.url:}') && ${datasources.enabled:false}}")
@EnableConfigurationProperties({DataSources.class})
@ComponentScan(basePackages = "com.gill.datasource")
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@AutoConfiguration
@Import(DataSourcesConfiguration.class)
public class AfterDataSourcesAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(
        AfterDataSourcesAutoConfiguration.class);

    @PostConstruct
    private void init() {
        log.info("load dynamic datasources after DataSourcesAutoConfiguration");
    }
}
