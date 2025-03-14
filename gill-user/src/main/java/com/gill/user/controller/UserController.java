package com.gill.user.controller;

import cn.hutool.core.lang.UUID;
import com.gill.api.domain.UserProperties;
import com.gill.common.exception.BusinessException;
import com.gill.common.threadlocal.ThreadLocals;
import com.gill.user.domain.UserDetail;
import com.gill.user.dto.CurrentUser;
import com.gill.user.dto.IndividualProfile;
import com.gill.user.dto.UserInfo;
import com.gill.user.dto.param.AdminRegisterParam;
import com.gill.user.dto.param.IndividualProfileParam;
import com.gill.user.dto.param.LoginParam;
import com.gill.user.dto.param.RegisterParam;
import com.gill.user.service.CaptchaService;
import com.gill.user.service.ResourceService;
import com.gill.user.service.UserService;
import com.gill.web.annotation.IgnoreAuth;
import com.gill.web.annotation.OperationPermission;
import com.gill.web.api.Response;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
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
public class UserController {

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private UserService userService;

    @Autowired
    private ResourceService resourceService;

    /**
     * 预校验用户名是否已存在
     *
     * @param username 用户名
     */
    @IgnoreAuth
    @GetMapping("/precheck/username")
    public Response<String> precheckUsername(@RequestParam("username") String username) {
        userService.precheckUsername(username);
        return Response.success().build();
    }

    /**
     * 注册接口
     *
     * @param param 参数
     * @return 响应
     */
    @IgnoreAuth
    @PostMapping("/register")
    public Response<String> register(@Validated @RequestBody RegisterParam param,
        HttpServletResponse response) {

        // 验证码校验
        String randomCode = param.getRandomCode();
        String captchaCode = param.getCaptchaCode();
        captchaService.checkCaptchaCode(randomCode, captchaCode);

        // 注册用户信息
        long userId = userService.registerUser(param);

        // 登录
        UserDetail userDetail = userService.successLoginAndGenerateToken(userId);
        addUserCookies(response, userId, userDetail);
        return Response.success(userDetail.getHome()).build();
    }

    /**
     * 管理员注册接口
     *
     * @param param 参数
     * @return 响应
     */
    @OperationPermission(permissionExpression = "permission.register")
    @PostMapping("/admin/register")
    public Response<String> adminRegister(@Validated @RequestBody AdminRegisterParam param) {

        // 校验角色ID是否正确
        if (!resourceService.containsRole(param.getRole())) {
            throw new BusinessException("角色不存在");
        }

        // 验证码校验
        String randomCode = param.getRandomCode();
        String captchaCode = param.getCaptchaCode();
        captchaService.checkCaptchaCode(randomCode, captchaCode);

        // 注册用户信息
        userService.registerUserWithRole(param, Set.of(param.getRole()));
        return Response.success().build();
    }

    /**
     * 登录接口
     *
     * @param param 参数
     * @return 响应
     */
    @IgnoreAuth
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
        long userId = userService.checkLogin(username, password);

        // 校验用户是否可登录
        userService.checkAccess(userId);

        // 成功登录后置处理
        UserDetail userDetail = userService.successLoginAndGenerateToken(userId);
        addUserCookies(response, userId, userDetail);
        return Response.success(userDetail.getHome()).build();
    }

    private static void addUserCookies(HttpServletResponse response, long userId,
        UserDetail userDetail) {
        response.addCookie(buildCookie(UserProperties.USER_ID, String.valueOf(userId)));
        response.addCookie(buildCookie(UserProperties.USER_NAME, userDetail.getUsername()));
        response.addCookie(buildCookie(UserProperties.TOKEN_ID, userDetail.getToken()));
        response.addCookie(
            buildCookie(UserProperties.CSRF_TOKEN, UUID.randomUUID().toString(true)));
    }

    private static Cookie buildCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setPath("/");
        return cookie;
    }

    /**
     * 登出
     *
     * @param userId   用户ID
     * @param response 响应
     * @return 响应
     */
    @PostMapping("/logout")
    public Response<String> logout(@RequestAttribute(UserProperties.USER_ID) long userId,
        @RequestAttribute(UserProperties.TOKEN_ID) String token, HttpServletResponse response) {
        userService.logout(userId, token);
        response.addCookie(clearCookie(UserProperties.USER_ID));
        response.addCookie(clearCookie(UserProperties.USER_NAME));
        response.addCookie(clearCookie(UserProperties.TOKEN_ID));
        response.addCookie(clearCookie(UserProperties.CSRF_TOKEN));
        return Response.success().build();
    }

    private static Cookie clearCookie(String key) {
        Cookie cookie = new Cookie(key, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }

//    /**
//     * 邀请码登录
//     *
//     * @param inviteCode 邀请码
//     * @return 登录结果
//     */
//    @IgnoreAuth
//    @PostMapping("/invite/login")
//    public Response<String> inviteCodeLogin(@RequestParam("inviteCode") String inviteCode) {
//
//        return Response.success().build();
//    }

    /**
     * 获取登录用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    public Response<UserInfo> userInfo() {
        Long userId = ThreadLocals.USER_ID.get();
        String token = ThreadLocals.TOKEN.get();
        UserInfo userInfo = userService.getUserInfo(userId, token);
        return Response.success(userInfo).build();
    }

    /**
     * 获取登录用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/current")
    public Response<CurrentUser> currentUser() {
        Long userId = ThreadLocals.USER_ID.get();
        String token = ThreadLocals.TOKEN.get();
        UserInfo userInfo = userService.getUserInfo(userId, token);
        Set<String> permissions = resourceService.getUserPermissions(userId);
        CurrentUser currentUser = new CurrentUser(userInfo, permissions);
        return Response.success(currentUser).build();
    }

//    /**
//     * 获取用户邀请码
//     *
//     * @return 邀请码
//     */
//    @GetMapping("/invite_key")
//    public Response<String> inviteKey() {
//        Long userId = ThreadLocals.USER_ID.get();
//        return Response.success(userService.getInviteKey(userId)).build();
//    }
//
//    /**
//     * 刷新用户邀请码
//     *
//     * @return void
//     */
//    @PostMapping("/invite_key")
//    public Response<String> refreshInviteKey() {
//        Long userId = ThreadLocals.USER_ID.get();
//        userService.refreshInviteKey(userId);
//        return Response.success().build();
//    }

    /**
     * 个人信息
     *
     * @return 个人信息
     */
    @GetMapping("/profile")
    public Response<IndividualProfile> getProfile() {
        Long userId = ThreadLocals.USER_ID.get();
        IndividualProfile profile = userService.getProfile(userId);
        return Response.success(profile).build();
    }

    /**
     * 修改个人信息
     *
     * @param params 参数
     * @return OK
     */
    @PostMapping("/profile")
    public Response<String> updateProfile(@RequestBody IndividualProfileParam params) {
        Long userId = ThreadLocals.USER_ID.get();
        userService.updateProfile(userId, params);
        return Response.success().build();
    }

    /**
     * 获取邀请注册码
     *
     * @return 邀请注册码
     */
    @GetMapping("/invite_key")
    public Response<String> getInviteKey() {
        Long userId = ThreadLocals.USER_ID.get();
        return Response.success(userService.getInviteKey(userId)).build();
    }

    /**
     * 刷新邀请注册码
     *
     * @return OK
     */
    @PostMapping("/invite_key")
    public Response<String> refreshInviteKey() {
        Long userId = ThreadLocals.USER_ID.get();
        userService.refreshInviteKey(userId);
        return Response.success().build();
    }
}
