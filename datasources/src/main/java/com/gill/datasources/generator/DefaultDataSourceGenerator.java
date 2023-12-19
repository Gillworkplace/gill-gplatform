package com.gill.datasources.generator;

import com.alibaba.druid.pool.DruidDataSource;
import com.gill.datasources.DataSourceProperties;
import com.gill.datasources.DataSources;
import com.gill.datasources.decryption.DecryptionStrategy;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;

/**
 * DruidDataSourceGenerator
 *
 * @author gill
 * @version 2023/12/18
 **/

class DefaultDataSourceGenerator extends BaseDataSourceGenerator {

    /**
     * 是否支持
     *
     * @param properties 属性
     * @return 是否支持
     */
    @Override
    boolean canHandle(DataSourceProperties properties) {
        return true;
    }

    /**
     * 生成Datasource
     *
     * @param datasources datasources
     * @param properties  属性
     * @return 数据源
     */
    @Override
    DataSource generate(DataSources datasources, DataSourceProperties properties) {
        DecryptionStrategy decryption = getDecryptionStrategy(properties,
            datasources.getDecryptionName());
        String pwd = getStrFromFile(properties.pwdFile(), properties.password());
        return DataSourceBuilder.create().url(properties.url())
            .driverClassName(properties.driverClassName()).username(properties.username())
            .password(decryption.decrypt(pwd)).type(DruidDataSource.class).build();
    }
}
