package com.gill.datasources.generator;

import com.alibaba.druid.pool.DruidDataSource;
import com.gill.datasources.config.DataSourceProperties;
import com.gill.datasources.decryption.DecryptionFactory;
import com.gill.datasources.decryption.DecryptionStrategy;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;

/**
 * DruidDataSourceGenerator
 *
 * @author gill
 * @version 2023/12/18
 **/

public class DefaultDataSourceGenerator extends BaseDataSourceGenerator {

    /**
     * 是否支持
     *
     * @param properties 属性
     * @return 是否支持
     */
    @Override
    public boolean canHandle(DataSourceProperties properties) {
        return true;
    }

    /**
     * 生成Datasource
     *
     * @param properties 属性
     * @return 数据源
     */
    @Override
    public DataSource generate(DataSourceProperties properties) {
        DecryptionStrategy decryption = DecryptionFactory.getStrategy(properties.decryptionName());
        String pwd = getStrFromFile(properties.pwdFile(), properties.password());
        return DataSourceBuilder.create().url(properties.url())
            .driverClassName(properties.driverClassName()).username(properties.username())
            .password(decryption.decrypt(pwd)).type(DruidDataSource.class).build();
    }
}
