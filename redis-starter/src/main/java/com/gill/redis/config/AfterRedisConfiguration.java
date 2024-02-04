package com.gill.redis.config;


import com.gill.common.api.DLock;
import com.gill.redis.core.Redis;
import com.gill.redis.core.RedisTemplateAdapter;
import com.gill.redis.core.RedissonDLockAdapter;
import com.gill.redis.fortest.RedisTemplateWindowAdapter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
    @ConditionalOnProperty(value = "redis.test", havingValue = "false", matchIfMissing = true)
    @ConditionalOnBean(name = {"stringRedisTemplate"})
    public Redis redis(StringRedisTemplate stringRedisTemplate) {
        return new RedisTemplateAdapter(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnProperty(value = "redis.test", havingValue = "true")
    @ConditionalOnBean(name = {"stringRedisTemplate"})
    public Redis redisForTest(StringRedisTemplate stringRedisTemplate) {
        return new RedisTemplateWindowAdapter(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(name = {"dLock"})
    public DLock dLock(RedissonClient client) {
        return new RedissonDLockAdapter(client);
    }
}
