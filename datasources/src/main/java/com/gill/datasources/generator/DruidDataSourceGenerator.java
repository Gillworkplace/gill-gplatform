package com.gill.datasources.generator;

import com.alibaba.druid.pool.DruidDataSource;
import com.gill.datasources.DataSourceProperties;
import com.gill.datasources.decryption.DecryptionFactory;
import com.gill.datasources.decryption.DecryptionStrategy;
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
     * @param properties 属性
     * @return 数据源
     */
    @Override
    DataSource generate(DataSourceProperties properties) {
        DecryptionStrategy decryption = DecryptionFactory.getStrategy(properties.decryptionName());
        String pwd = getStrFromFile(properties.pwdFile(), properties.password());
        DruidDataSource datasource = properties.druid();
        datasource.setUrl(properties.url());
        datasource.setDriverClassName(properties.driverClassName());
        datasource.setUsername(properties.username());
        datasource.setPassword(decryption.decrypt(pwd));
        return datasource;
    }
}
