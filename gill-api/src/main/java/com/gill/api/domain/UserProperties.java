package com.gill.api.domain;

/**
 * TokenProperties
 *
 * @author gill
 * @version 2024/01/30
 **/
public class UserProperties {

    /**
     * 用户ID
     */
    public static final String USER_ID = "uid";

    /**
     * tokenID
     */
    public static final String TOKEN_ID = "tid";

    /**
     * 用户名
     */
    public static final String USER_NAME = "username";

    /**
     * 昵称
     */
    public static final String NICK_NAME = "nickname";

    /**
     * 头像
     */
    public static final String AVATAR = "avatar";

    /**
     * 用户描述
     */
    public static final String DESCRIPTION = "description";

    /**
     * 验证码redis前缀
     */
    public static final String REDIS_CAPTCHA_PREFIX = "captcha" + RedisConstant.REDIS_KEY_SPLITTER;

    /**
     * 资源redis前缀
     */
    public static final String REDIS_RESOURCES_PREFIX =
        "resources" + RedisConstant.REDIS_KEY_SPLITTER;

    /**
     * 用户信息redis前缀
     */
    public static final String REDIS_USER_INFO_PREFIX = "user" + RedisConstant.REDIS_KEY_SPLITTER;

    /**
     * token redis前缀
     */
    public static final String REDIS_TOKEN_PREFIX = "token" + RedisConstant.REDIS_KEY_SPLITTER;
}
