package com.gill.api.domain;

/**
 * NotificationProperties
 *
 * @author gill
 * @version 2024/02/01
 **/
public class NotificationProperties {

    /**
     * uid -> server-id[] 的映射 的redis key 前缀
     */
    public static final String REDIS_USER_LOCATION_PREFIX =
        "notification" + RedisConstant.REDIS_KEY_SPLITTER + "server"
            + RedisConstant.REDIS_KEY_SPLITTER;

    /**
     * uid -> messages 的redis key 前缀
     */
    public static final String REDIS_USER_MESSAGES_PREFIX =
        "notification" + RedisConstant.REDIS_KEY_SPLITTER + "unread"
            + RedisConstant.REDIS_KEY_SPLITTER;

    /**
     * 用户的unread队列的redis lock
     */
    public static final String REDIS_USER_MSG_LOCK_PREFIX =
        RedisConstant.REDIS_LOCK_PREFIX + "unread" + RedisConstant.REDIS_KEY_SPLITTER;
}
