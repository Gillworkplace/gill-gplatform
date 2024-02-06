package com.gill.datasource.generator;

import com.gill.common.crypto.CryptoStrategy;
import com.gill.datasource.DataSourceProperties;
import com.gill.datasource.DataSources;
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
     * @param datasources datasource
     * @param properties  属性
     * @return 数据源
     */
    @Override
    DataSource generate(DataSources datasources, DataSourceProperties properties) {
        CryptoStrategy decryption = getDecryptionStrategy(properties,
            datasources.getDecryptionName());
        String pwd = getStrFromFile(properties.pwdFile(), properties.password());
        HikariConfig config = properties.hikari();
        config.setJdbcUrl(properties.url());
        config.setUsername(properties.username());
        config.setPassword(decryption.decrypt(pwd));
        config.setDriverClassName(properties.driverClassName());
        return new HikariDataSource(config);
    }
}
