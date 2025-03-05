package com.gill.datasource;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallFilter;
import com.gill.datasource.config.DruidConfig;
import com.gill.datasource.dynamic.DynamicDataSource;
import com.gill.datasource.generator.DataSourceGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

/**
 * DataSourcesAutoConfiguration
 * <p>
 * 当spring.datasource.url不为空时代表已经配置了数据源，此时多数据源需要在DataSourceAutoConfiguration后执行
 *
 * @author gill
 * @version 2023/12/18
 **/
public class DataSourcesConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DataSourcesConfiguration.class);

    @Autowired
    private DataSources datasources;

    private record DataSourceInfo(DataSource defaultDataSource,
                                  Map<String, DataSource> dataSources) {

    }

    private DataSourceInfo generateDataSources() {
        Map<String, DataSourceProperties> sources = datasources.getSources();
        Map<String, DataSource> dataSources = new HashMap<>();
        DataSource first = null;
        DataSource primary = null;
        for (Map.Entry<String, DataSourceProperties> entry : sources.entrySet()) {
            String name = entry.getKey();
            DataSourceProperties properties = entry.getValue();
            if (!properties.enabled()) {
                continue;
            }
            DataSource dataSource = DataSourceGenerator.chainsGenerate(datasources, properties);
            dataSources.put(name, dataSource);
            if (properties.druid() != null) {
                handleDruid(dataSource, properties);
            }
            if (first == null) {
                first = dataSource;
            }
            if (properties.isPrimary()) {
                primary = dataSource;
            }
            log.info("dynamic datasources load: {}", name);
        }
        return new DataSourceInfo(primary == null ? first : primary, dataSources);
    }

    private void handleDruid(DataSource dataSource, DataSourceProperties properties) {
        DruidDataSource ds = (DruidDataSource) dataSource;
        DruidConfig druidConfig = properties.druid();
        List<Filter> proxyFilters = new ArrayList<>();
        String filters = druidConfig.getFilters();
        if (StrUtil.isNotBlank(filters) && filters.contains("stat")) {
            proxyFilters.add(druidConfig.getStat());
        }
        if (StrUtil.isNotBlank(filters) && filters.contains("wall")) {
            WallFilter wallFilter = new WallFilter();
            wallFilter.setConfig(druidConfig.getWall());
            proxyFilters.add(wallFilter);
        }
        if (StrUtil.isNotBlank(filters) && filters.contains("slf4j")) {
            proxyFilters.add(druidConfig.getSlf4j());
        }
        ds.setProxyFilters(proxyFilters);
    }

    /**
     * 当spring.datasource.url不为空时代表已经配置了数据源， 就以该数据源为默认数据源
     *
     * @param primary spring数据源
     * @return 动
     */
    @Primary
    @Bean
    @DependsOn({"com.gill.datasource.MybatisConfiguration"})
    public DataSource dynamicDataSource(DataSource primary,
        @Value("${spring.datasource.url:}") String springDataSourceUrl) {
        if (StrUtil.isNotEmpty(springDataSourceUrl)) {
            log.info("create dynamicDataSource with spring datasource: {}", primary);
            return dynamicDataSource(primary);
        }
        log.info("create dynamicDataSource");
        return dynamicDataSource(null);
    }

    private DataSource dynamicDataSource(DataSource primary) {
        DataSourceInfo dataSourceInfo = generateDataSources();
        Map<Object, Object> dataSources = new HashMap<>(dataSourceInfo.dataSources());
        primary = primary == null ? dataSourceInfo.defaultDataSource() : primary;
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setDefaultTargetDataSource(primary);
        dynamicDataSource.setTargetDataSources(dataSources);
        return dynamicDataSource;
    }
}
