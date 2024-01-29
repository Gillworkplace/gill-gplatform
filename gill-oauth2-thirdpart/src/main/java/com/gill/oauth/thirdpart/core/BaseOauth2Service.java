package com.gill.oauth.thirdpart.core;

import com.gill.oauth.thirdpart.domain.Token;
import com.gill.oauth.thirdpart.domain.UserInfo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

/**
 * Oauth2Service
 *
 * @author gill
 * @version 2024/01/19
 **/
@Slf4j
public abstract class BaseOauth2Service {

    private static class Client {
        private static final OkHttpClient INSTANCE = new OkHttpClient();
    }

    protected OkHttpClient getClient() {
        return Client.INSTANCE;
    }

    /**
     * 三方登录界面的重定向URL
     * 
     * @return url
     */
    public abstract String authRedirectUrl(String extraMessage) throws Exception;

    /**
     * 根据userId 刷新三方用户token
     * 
     * @param userId 用户ID
     */
    public abstract void refreshToken(String userId) throws Exception;

    /**
     * 授权回调
     *
     * @param params 授权码
     */
    public UserInfo authCallback(String... params) throws Exception {
        try {
            Token ac = obtainAccessToken(params);
            return obtainUserInfo(ac);
        } catch (Exception e) {
            log.error("call auth callback failed, e: {}", e.getMessage());
            throw new Exception("call auth callback failed");
        }
    }

    protected abstract Token obtainAccessToken(String... params) throws Exception;

    protected abstract UserInfo obtainUserInfo(Token ac) throws Exception;
}
