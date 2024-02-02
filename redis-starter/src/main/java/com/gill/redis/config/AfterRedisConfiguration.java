package com.gill.redis.config;


import com.gill.common.api.DLock;
import com.gill.redis.core.Redis;
import com.gill.redis.core.RedisTemplateAdapter;
import com.gill.redis.core.RedissonDLockAdapter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

    @Bean
    @ConditionalOnMissingBean(name = {"dLock"})
    public DLock dLock(RedissonClient client) {
        return new RedissonDLockAdapter(client);
    }
}
