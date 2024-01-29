package com.gill.oauth.thirdpart.core.wechat;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gill.oauth.thirdpart.core.BaseOauth2Service;
import com.gill.oauth.thirdpart.domain.Token;
import com.gill.oauth.thirdpart.domain.UserInfo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * WechatOatuh2ServiceImpl
 *
 * @author gill
 * @version 2024/01/19
 **/
@ConditionalOnProperty(prefix = "oauth.wechat", name = "enabled", havingValue = "true")
@Component("wechatService")
@Slf4j
public class WechatOauth2Service extends BaseOauth2Service {

    private static Validator VALIDATOR;

    static {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            VALIDATOR = factory.getValidator();
        } catch (Exception e) {
            log.error("validator util init failed. reason: {}", e.getMessage());
        }
    }

    @Value("${oauth.wechat.redirectUri}")
    private String redirectUri;

    @Value("${oauth.wechat.authUrl}")
    private String authUrl;

    @Value("${oauth.wechat.accessTokenUrl}")
    private String accessTokenUrl;

    @Value("${oauth.wechat.refreshTokenUrl}")
    private String refreshTokenUrl;

    @Value("${oauth.wechat.userInfoUrl}")
    private String userInfoUrl;

    /**
     * 三方登录界面的重定向URL
     *
     * @return url
     */
    @Override
    public String authRedirectUrl(String extraMessage) throws Exception {
        String encodeRedirectUrl = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        return MessageFormatter.arrayFormat(authUrl, new String[]{encodeRedirectUrl, extraMessage})
            .getMessage();
    }

    /**
     * 根据userId 刷新三方用户token
     *
     * @param userId 用户ID
     */
    @Override
    public void refreshToken(String userId) throws Exception {

    }

    @Override
    protected Token obtainAccessToken(String... params) throws Exception {
        Request request = new Builder().url(getAccessTokenUrl(params)).get().build();
        Call call = getClient().newCall(request);
        try (Response response = call.execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new Exception("obtain access token failed, code: " + response.code());
            }
            ResponseBody body = response.body();
            return parseAccessToken(body.string());
        }
    }

    private String getAccessTokenUrl(String... params) {
        return MessageFormatter.arrayFormat(accessTokenUrl, params).getMessage();
    }

    private Token parseAccessToken(String content) {
        JSONObject obj = JSONUtil.parseObj(content);
        Token token = new Token();
        token.setOpenUserId(obj.get("openid", String.class));
        token.setAccessToken(obj.get("access_token", String.class));
        token.setRefreshToken(obj.get("refresh_token", String.class));
        Set<ConstraintViolation<Token>> cs = VALIDATOR.validate(token);
        if (!cs.isEmpty()) {
            throw new IllegalArgumentException("access token is illegal: " + content);
        }

        return token;
    }

    @Override
    protected UserInfo obtainUserInfo(Token token) throws Exception {
        Request request = new Builder().url(
            getUserInfoUrl(token.getAccessToken(), token.getOpenUserId())).get().build();
        Call call = getClient().newCall(request);
        try (Response response = call.execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new Exception("obtain user info failed, code: " + response.code());
            }
            ResponseBody body = response.body();
            return parseUserInfo(token, body.string());
        }
    }

    private String getUserInfoUrl(String... params) {
        return MessageFormatter.arrayFormat(userInfoUrl, params).getMessage();
    }

    private UserInfo parseUserInfo(Token token, String content) {
        JSONObject obj = JSONUtil.parseObj(content);
        UserInfo userInfo = new UserInfo();
        userInfo.setToken(token);
        userInfo.setOpenUserId(obj.get("openid", String.class));
        userInfo.setNickName(obj.get("nickname", String.class));
        userInfo.setHeadImgUrl(obj.get("headimgurl", String.class));
        userInfo.setPrivilege((List<String>) obj.get("privilege"));
        userInfo.setUnionId(obj.get("unionId", String.class));
        return userInfo;
    }
}
