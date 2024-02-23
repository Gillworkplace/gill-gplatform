package com.gill.redis;

import com.gill.redis.config.BeforeRedisConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * RedisAutoConfiguration
 *
 * @author gill
 * @version 2024/01/29
 **/
@AutoConfigureBefore(org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class)
@Import(BeforeRedisConfiguration.class)
@Slf4j
public class BeforeRedisAutoConfiguration {

    @PostConstruct
    private void init() {
        log.info("before redis auto configuration action");
    }
}
