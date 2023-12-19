package com.gill.datasources;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Datasource
 *
 * @author gill
 * @version 2023/12/18
 **/
public record DataSourceProperties(@DefaultValue("false") boolean isPrimary,
                                   @DefaultValue("true") boolean enabled, String driverClassName,
                                   String url, String username, String password, String pwdFile,
                                   String decryptionName, DruidDataSource druid,
                                   HikariConfig hikari) {

}
