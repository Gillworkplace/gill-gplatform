package com.gill.datasources.dynamic;

/**
 * DynamicDataSourceContextHolder
 *
 * @author gill
 * @version 2023/12/19
 **/
public class DynamicDataSourceSelector {

    public static final String DEFAULT_DB = "default-db";

    /**
     * 动态数据源名称上下文
     */
    private static final ThreadLocal<String> DATASOURCE_BEAN_NAME = new ThreadLocal<>();

    /**
     * 设置数据源名称
     *
     * @param dataSourceBeanName 数据源名称
     */
    public static void use(String dataSourceBeanName) {
        DATASOURCE_BEAN_NAME.set(dataSourceBeanName);
    }

    /**
     * 获取数据源名称
     *
     * @return 数据源名称
     */
    public static String getTarget() {
        String key = DATASOURCE_BEAN_NAME.get();
        return key == null ? DEFAULT_DB : key;
    }

    /**
     * 删除当前数据源名称
     */
    public static void remove() {
        DATASOURCE_BEAN_NAME.remove();
    }
}
