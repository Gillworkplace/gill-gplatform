package com.gill.datasources.generator;

import com.gill.datasources.DataSourceProperties;
import com.gill.datasources.DataSources;
import com.gill.datasources.decryption.DecryptionFactory;
import com.gill.datasources.decryption.DecryptionStrategy;
import com.google.common.io.Files;
import java.io.IOException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

/**
 * DataSourceGenerator
 *
 * @author gill
 * @version 2023/12/18
 **/
abstract class BaseDataSourceGenerator {

    private static final Logger log = LoggerFactory.getLogger(BaseDataSourceGenerator.class);

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
     * @param datasources datasources
     * @param properties  属性
     * @return 数据源
     */
    abstract DataSource generate(DataSources datasources, DataSourceProperties properties);

    /**
     * 获取解密策略
     *
     * @param properties            数据源属性
     * @param defaultDecryptionName 默认解密名称
     * @return 解密策略
     */
    protected DecryptionStrategy getDecryptionStrategy(DataSourceProperties properties,
        String defaultDecryptionName) {
        String decryptionName = properties.decryptionName();
        if (decryptionName == null) {
            return DecryptionFactory.getStrategy(defaultDecryptionName);
        }
        return DecryptionFactory.getStrategy(decryptionName);
    }

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
            return new String(Files.toByteArray(ResourceUtils.getFile(strFile)));
        } catch (IOException e) {
            log.error("get str from file failed, e: {}", e.getMessage());
            return defaultStr;
        }
    }
}
