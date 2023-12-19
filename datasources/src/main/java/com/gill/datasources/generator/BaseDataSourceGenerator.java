package com.gill.datasources.generator;

import com.gill.datasources.DataSourceProperties;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import javax.sql.DataSource;

/**
 * DataSourceGenerator
 *
 * @author gill
 * @version 2023/12/18
 **/
 abstract class BaseDataSourceGenerator {

    /**
     * 是否支持
     *
     * @param properties 属性
     * @return 是否支持
     */
    abstract boolean canHandle(DataSourceProperties properties);

    /**
     * 生成Datasource
     *
     * @param properties 属性
     * @return 数据源
     */
    abstract DataSource generate(DataSourceProperties properties);

    /**
     * 从文件中获取字符串
     *
     * @param strFile    字符串文件
     * @param defaultStr 默认字符串
     * @return str
     */
    protected String getStrFromFile(String strFile, String defaultStr) {
        if (strFile == null) {
            return defaultStr;
        }
        try {
            return new String(Files.toByteArray(new File(strFile)));
        } catch (IOException e) {
            return defaultStr;
        }
    }
}
