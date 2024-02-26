package com.gill.oss.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OssConfig
 *
 * @author gill
 * @version 2024/02/26
 **/
@Configuration
@EnableConfigurationProperties(OssProperty.class)
@Slf4j
public class OssConfig {

    public static final String OSS_ENDPOINT = "ALIYUN_OSS_ENDPOINT";

    public static final String ALIYUN_OSS_ACCESS_KEY_ID = "ALIYUN_OSS_ACCESS_KEY_ID";

    public static final String ALIYUN_OSS_ACCESS_KEY_SECRET = "ALIYUN_OSS_ACCESS_KEY_SECRET";

    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(System.getenv(OSS_ENDPOINT),
            System.getenv(ALIYUN_OSS_ACCESS_KEY_ID), System.getenv(ALIYUN_OSS_ACCESS_KEY_SECRET));
    }

}
