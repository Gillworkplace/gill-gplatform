package com.gill.redis;

import com.gill.redis.config.AfterRedisConfiguration;
import com.gill.redis.config.BeforeRedisConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConfiguration;

/**
 * RedisAutoConfiguration
 *
 * @author gill
 * @version 2024/01/29
 **/
@AutoConfiguration
@AutoConfigureAfter(org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class)
@Import(AfterRedisConfiguration.class)
@Slf4j
public class AfterRedisAutoConfiguration {

    @PostConstruct
    private void init() {
        log.info("after redis auto configuration action");
    }
}
