package com.gill.datasource.config;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * DruidConfig
 *
 * @author zhangzhiyan
 * @since 2025-03-05
 */
@Getter
@Setter
public class DruidConfig extends DruidDataSource {

    private String filters;

    @NestedConfigurationProperty
    private WallConfig wall = new WallConfig();

    @NestedConfigurationProperty
    private StatFilter stat = new StatFilter();

    @NestedConfigurationProperty
    private Slf4jLogFilter slf4j = new Slf4jLogFilter();
}
