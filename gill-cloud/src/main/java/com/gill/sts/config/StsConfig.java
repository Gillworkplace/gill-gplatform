package com.gill.sts.config;

import com.aliyun.sts20150401.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * StsConfig
 *
 * @author gill
 * @version 2024/02/21
 **/
@Configuration
public class StsConfig {

    private static final String ACCESS_KEY_ID = "ALIYUN_OSS_ACCESS_KEY_ID";

    private static final String ACCESS_KEY_SECRET = "ALIYUN_OSS_ACCESS_KEY_SECRET";

    private static final String STS_ENDPOINT = "ALIYUN_STS_ENDPOINT";

    @Bean
    public Client stsClient() throws Exception {
        Config config = new Config().setAccessKeyId(System.getenv(ACCESS_KEY_ID))
            .setAccessKeySecret(System.getenv(ACCESS_KEY_SECRET));
        config.setEndpoint(System.getenv(STS_ENDPOINT));
        return new Client(config);
    }
}
