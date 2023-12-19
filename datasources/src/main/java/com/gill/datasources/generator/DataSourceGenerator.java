package com.gill.datasources.generator;

import com.gill.datasources.DataSourceProperties;
import com.gill.datasources.DataSources;
import javax.sql.DataSource;

/**
 * DataSourceGenerator
 *
 * @author gill
 * @version 2023/12/18
 **/
public enum DataSourceGenerator {

    /**
     * Druid
     */
    DRUID(new DruidDataSourceGenerator()),

    /**
     * Hikari
     */
    HIKARI(new HikariDataSourceGenerator()),

    /**
     * SingleConnectionDataSource
     */
    DEFAULT(new DefaultDataSourceGenerator());

    private final BaseDataSourceGenerator internal;

    DataSourceGenerator(BaseDataSourceGenerator internal) {
        this.internal = internal;
    }

    /**
     * 生成dataSource
     *
     * @param datasources 数据源
     * @param properties  属性配置
     * @return datasource
     */
    public static DataSource chainsGenerate(DataSources datasources,
        DataSourceProperties properties) {
        for (DataSourceGenerator generator : DataSourceGenerator.values()) {
            if (generator.internal.canHandle(properties)) {
                return generator.internal.generate(datasources, properties);
            }
        }
        throw new IllegalArgumentException("can not find datasource generator");
    }
}
