package com.gill.user.controller;

import com.gill.user.dto.LoginParam;
import com.gill.user.dto.RegisterParam;
import com.gill.user.service.CaptchaService;
import com.gill.user.service.UserService;
import com.gill.web.api.Response;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册登录相关接口
 *
 * @author gill
 * @version 2024/02/06
 **/
@RestController
public class LoginController {

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private UserService userService;

    /**
     * 预校验用户名是否已存在
     *
     * @param username 用户名
     */
    @GetMapping("/precheck/username")
    public void precheckUsername(@RequestParam("username") String username) {
        userService.precheckUsername(username);
    }

    /**
     * 注册接口
     *
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/register")
    public Response<String> register(@Validated @RequestBody RegisterParam param,
        HttpServletResponse response) {

        // 验证码校验
        String randomCode = param.getRandomCode();
        String captchaCode = param.getCaptchaCode();
        captchaService.checkCaptchaCode(randomCode, captchaCode);

        // 注册用户信息
        int userId = userService.registerUser(param);

        // 登录
        String token = userService.successLoginAndGenerateToken(userId);
        Cookie cookie = buildTokenCookie(token);
        response.addCookie(cookie);
        return Response.success("/home").build();
    }

    /**
     * 登录接口
     *
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/login")
    public Response<String> login(@Validated @RequestBody LoginParam param,
        HttpServletResponse response) {

        // 校验验证码
        String randomCode = param.getRandomCode();
        String captchaCode = param.getCaptchaCode();
        captchaService.checkCaptchaCode(randomCode, captchaCode);

        // 校验用户名密码
        String username = param.getUsername();
        String password = param.getPassword();
        int userId = userService.checkLogin(username, password);

        // 校验用户是否可登录
        userService.checkAccess(userId);

        // 成功登录后置处理
        String token = userService.successLoginAndGenerateToken(userId);

        Cookie cookie = buildTokenCookie(token);
        response.addCookie(cookie);
        return Response.success("/home").build();
    }

    private static Cookie buildTokenCookie(String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setPath("/");
        return cookie;
    }

    /**
     * 邀请码登录
     *
     * @param inviteCode 邀请码
     * @return 登录结果
     */
    @PostMapping("/invite/login")
    public Response<String> inviteCodeLogin(@RequestParam("inviteCode") String inviteCode) {

        return Response.success().build();
    }
}
