package com.gill.redis.fortest;

import cn.hutool.json.JSONUtil;
import com.gill.redis.core.RedisTemplateAdapter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;

/**
 * RedisTemplateOperator
 *
 * @author gill
 * @version 2024/01/29
 **/
@Slf4j
public class RedisTemplateWindowAdapter extends RedisTemplateAdapter {

    private final StringRedisTemplate redisTemplate;

    public RedisTemplateWindowAdapter(StringRedisTemplate redisTemplate) {
        super(redisTemplate);
        this.redisTemplate = redisTemplate;
    }

    /**
     * list head pop
     *
     * @param key   key
     * @param count count
     * @return els
     */
    @NonNull
    @Override
    public List<String> lpop(@NonNull String key, int count) {
        List<String> els = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String el = redisTemplate.opsForList().leftPop(key);
            if (el == null) {
                break;
            }
            els.add(el);
        }
        return els;
    }

    /**
     * list head pop
     *
     * @param key   key
     * @param clazz type
     * @param count count
     * @return els
     */
    @NonNull
    @Override
    public <T> List<T> lpop(@NonNull String key, Class<T> clazz, int count) {
        List<T> els = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String el = redisTemplate.opsForList().leftPop(key);
            if (el == null) {
                break;
            }
            els.add(JSONUtil.toBean(el, clazz));
        }
        return els;
    }

    /**
     * list tail pop
     *
     * @param key   key
     * @param count count
     * @return els
     */
    @NonNull
    @Override
    public List<String> lrpop(@NonNull String key, int count) {
        List<String> els = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String el = redisTemplate.opsForList().rightPop(key);
            if (el == null) {
                break;
            }
            els.add(el);
        }
        return els;
    }

    /**
     * list tail pop
     *
     * @param key   key
     * @param clazz type
     * @param count count
     * @return els
     */
    @NonNull
    @Override
    public <T> List<T> lrpop(@NonNull String key, Class<T> clazz, int count) {
        List<T> els = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String el = redisTemplate.opsForList().rightPop(key);
            if (el == null) {
                break;
            }
            els.add(JSONUtil.toBean(el, clazz));
        }
        return els;
    }
}
