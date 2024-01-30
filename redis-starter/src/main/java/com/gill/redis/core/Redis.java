package com.gill.redis.core;

import java.util.Map;

/**
 * Redis
 *
 * @author gill
 * @version 2024/01/29
 **/
public interface Redis {

    /**
     * set
     *
     * @param key   key
     * @param value value
     */
    void set(String key, Object value);

    /**
     * get
     *
     * @param key key
     * @return value
     */
    String get(String key);

    /**
     * get
     *
     * @param key   key
     * @param clazz type
     * @param <T>   type
     * @return value
     */
    <T> T get(String key, Class<T> clazz);
}
