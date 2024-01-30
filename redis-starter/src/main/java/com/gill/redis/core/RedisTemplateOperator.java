package com.gill.redis.core;

import cn.hutool.json.JSONUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * RedisTemplateOperator
 *
 * @author gill
 * @version 2024/01/29
 **/
@Slf4j
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
        if (value == null) {
            redisTemplate.delete(key);
        }
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

    /**
     * 设置map
     *
     * @param key key
     * @param map map
     */
    @Override
    public void setMap(String key, Map<String, Object> map) {
        if (map.isEmpty()) {
            return;
        }
        Map<String, String> serializedMap = new HashMap<>(map.size());
        for (Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String str) {
                serializedMap.put(entry.getKey(), str);
            } else {
                serializedMap.put(entry.getKey(), JSONUtil.toJsonStr(value));
            }
        }
        redisTemplate.opsForHash().putAll(key, serializedMap);
    }

    /**
     * 设置map
     *
     * @param key key
     * @param k   k
     * @param v   v
     */
    @Override
    public void setMap(String key, String k, Object v) {
        if (v == null) {
            redisTemplate.opsForHash().delete(key, k);
        }
        String val;
        if (v instanceof String str) {
            val = str;
        } else {
            val = JSONUtil.toJsonStr(v);
        }
        redisTemplate.opsForHash().put(key, k, val);
    }

    /**
     * 获取map
     *
     * @param key key
     * @return map
     */
    @Override
    public Map<String, Object> getMap(String key) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Object> map = new HashMap<>(entries.size());
        for (Entry<Object, Object> entry : entries.entrySet()) {
            map.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return map;
    }

    /**
     * 获取map
     *
     * @param key   key
     * @param clazz clazz
     * @param <T>   type
     * @return map
     */
    @Override
    public <T> Map<String, T> getMap(String key, Class<T> clazz) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, T> map = new HashMap<>(entries.size());
        for (Entry<Object, Object> entry : entries.entrySet()) {
            String val = String.valueOf(entry.getValue());
            String k = String.valueOf(entry.getKey());
            try {
                map.put(k, JSONUtil.toBean(val, clazz));
            } catch (Exception e) {
                map.put(k, null);
                log.error("redis.getMap(String, Class) parse obj to {}, failed, k: {}, v: {}",
                    clazz.getCanonicalName(), k, val);
            }
        }
        return map;
    }

    /**
     * 获取map
     *
     * @param key key
     * @param k   k
     * @return v
     */
    @Override
    public String getMap(String key, String k) {
        return String.valueOf(redisTemplate.opsForHash().get(key, k));
    }

    /**
     * 获取map
     *
     * @param key   key
     * @param k     k
     * @param clazz clazz
     * @return v
     */
    @Override
    public <T> T getMap(String key, String k, Class<T> clazz) {
        Object val = redisTemplate.opsForHash().get(key, k);
        return JSONUtil.toBean(String.valueOf(val), clazz);
    }

    /**
     * 获取map
     *
     * @param key key
     * @param ks  ks
     * @return vs
     */
    @Override
    public <T> Map<String, T> getMap(String key, Set<String> ks, Class<T> clazz) {
        List<Object> ksList = new ArrayList<>(ks);
        List<Object> values = redisTemplate.opsForHash().multiGet(key, ksList);
        Map<String, T> map = new HashMap<>(ksList.size());
        for (int i = 0; i < ksList.size(); i++) {
            String k = String.valueOf(ksList.get(i));
            String v = String.valueOf(values.get(i));
            try {
                map.put(k, JSONUtil.toBean(v, clazz));
            } catch (Exception e) {
                map.put(k, null);
                log.error("redis.getMap(String, Set, Class) parse obj to {}, failed, k: {}, v: {}",
                    clazz.getCanonicalName(), k, v);
            }
        }
        return map;
    }

    /**
     * 获取map
     *
     * @param key key
     * @param ks  ks
     * @return vs
     */
    @Override
    public Map<String, String> getMap(String key, Set<String> ks) {
        List<Object> ksList = new ArrayList<>(ks);
        List<Object> values = redisTemplate.opsForHash().multiGet(key, ksList);
        Map<String, String> map = new HashMap<>(ksList.size());
        for (int i = 0; i < ksList.size(); i++) {
            map.put(String.valueOf(ksList.get(i)), String.valueOf(values.get(i)));
        }
        return map;
    }
}
