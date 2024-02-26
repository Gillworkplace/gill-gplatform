package com.gill.api.domain;

/**
 * RedisConstant
 *
 * @author gill
 * @version 2024/02/01
 **/
public class RedisConstant {

    /**
     * redis key 分隔符
     */
    public static final String REDIS_KEY_SPLITTER = ":";

    /**
     * redis lock key 前缀
     */
    public static final String REDIS_LOCK_PREFIX = "lock" + REDIS_KEY_SPLITTER;
}
