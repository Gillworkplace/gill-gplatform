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
    private static final String REDIS_CAPTCHA_PREFIX = "captcha" + RedisConstant.REDIS_KEY_SPLITTER;

    /**
     * 用户资源redis前缀
     */
    private static final String REDIS_USER_RESOURCE_PREFIX =
        "user_resource" + RedisConstant.REDIS_KEY_SPLITTER;

    /**
     * 用户信息redis前缀
     */
    private static final String REDIS_USER_INFO_PREFIX = "user" + RedisConstant.REDIS_KEY_SPLITTER;

    /**
     * token redis前缀
     */
    private static final String REDIS_TOKEN_PREFIX = "token" + RedisConstant.REDIS_KEY_SPLITTER;

    /**
     * redis 用户ID生成器
     */
    public static final String REDIS_USER_ID_KEY = "user_id_generator";

    public static String getRedisCaptchaKey(String randomCode) {
        return REDIS_CAPTCHA_PREFIX + randomCode;
    }

    public static String getRedisTokenKey(String token) {
        return REDIS_TOKEN_PREFIX + token;
    }

    public static String getRedisUserInfoKey(int userId) {
        return REDIS_USER_INFO_PREFIX + userId;
    }

    public static String getRedisUserResourceKey(int userId) {
        return REDIS_USER_RESOURCE_PREFIX + userId;
    }
}
