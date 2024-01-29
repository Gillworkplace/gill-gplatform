package com.gill.oauth.thirdpart.controller;


import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.gill.oauth.thirdpart.core.BaseOauth2Service;
import com.gill.oauth.thirdpart.domain.UserInfo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * PrintController
 *
 * @author gill
 * @version 2024/01/18
 **/
@RestController
@RequestMapping("oauth2")
@Slf4j
public class Oauth2Controller {

    @Autowired
    private INotificationService notificationService;

    @RequestMapping("/{type}/auth")
    public void auth(@PathVariable("type") String type, HttpServletResponse response)
        throws Exception {
        BaseOauth2Service oauth2Service = getOauth2Service(type);
        String redirectUrl = oauth2Service.authRedirectUrl("test");
        response.sendRedirect(redirectUrl);
    }

    @RequestMapping("/{type}/auth/callback")
    public void callback(@PathVariable("type") String type,
        @RequestParam(value = "code") String code, @RequestParam("state") String state)
        throws Exception {
        log.info("wechat callback, code: {}, state: {}", code, state);
        BaseOauth2Service oauth2Service = getOauth2Service(type);
        UserInfo userInfo = oauth2Service.authCallback(code);
        log.info("user info: {}", userInfo);
        notificationService.send(state, "wxLogin", JSONUtil.toJsonStr(userInfo));
    }

    private static BaseOauth2Service getOauth2Service(String type) {
        BaseOauth2Service oauth2Service;
        try {
            oauth2Service = SpringUtil.getBean(type + "Service", BaseOauth2Service.class);
        } catch (Exception e) {
            throw new RuntimeException("unsupported auth, type: " + type);
        }
        return oauth2Service;
    }
}
