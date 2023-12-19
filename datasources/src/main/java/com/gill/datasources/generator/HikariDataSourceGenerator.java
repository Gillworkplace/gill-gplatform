package com.gill.datasources.generator;

import com.gill.datasources.DataSourceProperties;
import com.gill.datasources.decryption.DecryptionFactory;
import com.gill.datasources.decryption.DecryptionStrategy;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

/**
 * DruidDataSourceGenerator
 *
 * @author gill
 * @version 2023/12/18
 **/
class HikariDataSourceGenerator extends BaseDataSourceGenerator {

    /**
     * 是否支持
     *
     * @param properties 属性
     * @return 是否支持
     */
    @Override
    boolean canHandle(DataSourceProperties properties) {
        return properties.hikari() != null;
    }

    /**
     * 生成Datasource
     *
     * @param properties 属性
     * @return 数据源
     */
    @Override
    DataSource generate(DataSourceProperties properties) {
        DecryptionStrategy decryption = DecryptionFactory.getStrategy(properties.decryptionName());
        String pwd = getStrFromFile(properties.pwdFile(), properties.password());
        HikariConfig config = properties.hikari();
        config.setJdbcUrl(properties.url());
        config.setUsername(properties.username());
        config.setPassword(decryption.decrypt(pwd));
        config.setDriverClassName(properties.driverClassName());
        return new HikariDataSource(config);
    }
}
