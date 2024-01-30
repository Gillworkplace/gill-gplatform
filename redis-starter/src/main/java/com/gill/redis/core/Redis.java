package com.gill.redis.core;

import java.util.Map;
import java.util.Set;

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

    /**
     * 设置map
     *
     * @param key key
     * @param map map
     */
    void setMap(String key, Map<String, Object> map);

    /**
     * 设置map
     *
     * @param key key
     * @param k   k
     * @param v   v
     */
    void setMap(String key, String k, Object v);

    /**
     * 获取map
     *
     * @param key key
     * @return map
     */
    Map<String, Object> getMap(String key);

    /**
     * 获取map
     *
     * @param key   key
     * @param clazz clazz
     * @param <T>   type
     * @return map
     */
    <T> Map<String, T> getMap(String key, Class<T> clazz);

    /**
     * 获取map
     *
     * @param key key
     * @param k   k
     * @return v
     */
    String getMap(String key, String k);

    /**
     * 获取map
     *
     * @param key   key
     * @param k     k
     * @param clazz clazz
     * @param <T>   type
     * @return v
     */
    <T> T getMap(String key, String k, Class<T> clazz);

    /**
     * 获取map
     *
     * @param key   key
     * @param ks    ks
     * @param clazz clazz
     * @param <T>   type
     * @return vs
     */
    <T> Map<String, T> getMap(String key, Set<String> ks, Class<T> clazz);

    /**
     * 获取map
     *
     * @param key key
     * @param ks  ks
     * @return vs
     */
    Map<String, String> getMap(String key, Set<String> ks);
}
