package com.gill.redis.config;


import com.gill.redis.core.Redis;
import com.gill.redis.core.RedisTemplateAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * RedisConfig
 *
 * @author gill
 * @version 2024/01/29
 **/
public class AfterRedisConfiguration {

    @Bean
    @ConditionalOnBean(name = {"stringRedisTemplate"})
    public Redis redis(StringRedisTemplate stringRedisTemplate) {
        return new RedisTemplateAdapter(stringRedisTemplate);
    }
}
