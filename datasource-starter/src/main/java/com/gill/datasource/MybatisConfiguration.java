package com.gill.datasource;

import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;

/**
 * CustomMybatisAutoConfiguration
 *
 * @author gill
 * @version 2024/02/23
 **/
@Slf4j
public class MybatisConfiguration {

    public MybatisConfiguration(MybatisProperties mybatisProperties) {
        rewriteDefaultProperties(mybatisProperties);
    }

    private void rewriteDefaultProperties(MybatisProperties mybatisProperties) {
        log.info("rewrite mybatis properties");
        if (ArrayUtil.isEmpty(mybatisProperties.getMapperLocations())) {
            mybatisProperties.setMapperLocations(
                new String[]{"classpath*:mappers/*.xml", "classpath*:mapper/*.xml"});
        }
    }
}

