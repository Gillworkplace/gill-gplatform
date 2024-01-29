package com.gill.datasource.generator;

import com.alibaba.druid.pool.DruidDataSource;
import com.gill.common.decryption.DecryptionStrategy;
import com.gill.datasource.DataSourceProperties;
import com.gill.datasource.DataSources;
import javax.sql.DataSource;

/**
 * DruidDataSourceGenerator
 *
 * @author gill
 * @version 2023/12/18
 **/
class DruidDataSourceGenerator extends BaseDataSourceGenerator {

    /**
     * 是否支持
     *
     * @param properties 属性
     * @return 是否支持
     */
    @Override
    boolean canHandle(DataSourceProperties properties) {
        return properties.druid() != null;
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
        DruidDataSource datasource = properties.druid();
        datasource.setUrl(properties.url());
        datasource.setDriverClassName(properties.driverClassName());
        datasource.setUsername(properties.username());
        datasource.setPassword(decryption.decrypt(pwd));
        return datasource;
    }
}
