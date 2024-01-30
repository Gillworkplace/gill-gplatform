package com.gill.redis.core;

import cn.hutool.json.JSONUtil;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * RedisTemplateOperator
 *
 * @author gill
 * @version 2024/01/29
 **/
public class RedisTemplateOperator implements Redis {

    private final StringRedisTemplate redisTemplate;

    public RedisTemplateOperator(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * set
     *
     * @param key   key
     * @param value value
     */
    @Override
    public void set(String key, Object value) {
        String valueStr;
        if (value instanceof String str) {
            valueStr = str;
        } else {
            valueStr = JSONUtil.toJsonStr(value);
        }
        redisTemplate.opsForValue().set(key, valueStr);
    }


    /**
     * get
     *
     * @param key key
     * @return value
     */
    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * get
     *
     * @param key   key
     * @param clazz type
     * @return value
     */
    @Override
    public <T> T get(String key, Class<T> clazz) {
        String value = redisTemplate.opsForValue().get(key);
        return JSONUtil.toBean(value, clazz);
    }
}
