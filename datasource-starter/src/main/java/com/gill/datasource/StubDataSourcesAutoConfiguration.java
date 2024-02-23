package com.gill.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

/**
 * StubDataSourcesAutoConfiguration
 * <p>
 * 提前检测spring.datasource.url有没有配置，没有配置则创建一个stub datasource
 *
 * @author gill
 * @version 2023/12/18
 **/
@ConditionalOnExpression("#{T(cn.hutool.core.util.StrUtil).isEmpty('${spring.datasource.url:}')}")
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class StubDataSourcesAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(
        StubDataSourcesAutoConfiguration.class);

    @Bean
    public DataSource dataSourceStub() {
        log.info("create stub datasource because cannot find spring.datasource");
        return DataSourceBuilder.create()
            .url("localhost:3306")
            .driverClassName("com.mysql.cj.jdbc.Driver")
            .username("")
            .password("")
            .type(DruidDataSource.class)
            .build();
    }
}
