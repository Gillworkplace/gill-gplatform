package com.gill.user;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReflectUtil;
import com.gill.api.domain.UserProperties;
import com.gill.user.controller.ResourceController;
import com.gill.user.dto.CurrentUser;
import com.gill.user.dto.UserInfo;
import com.gill.user.dto.param.AdminRegisterParam;
import com.gill.user.dto.param.LoginParam;
import com.gill.user.dto.param.RegisterParam;
import com.gill.user.service.CaptchaService;
import com.gill.user.service.UserService;
import com.gill.web.api.Response;
import com.gill.web.api.Response.ResultWrapper;
import com.gill.web.exception.WebException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * 10以后代表登录完成 100以后代表退出登录
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.config.location=classpath:application-user.yaml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTests extends AbstractTest {

    public static final String URL_PREFIX = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private UserService userService;

    @Autowired
    public UserTests(ResourceController resourceController) {
        super.resourceController = resourceController;
    }

    @Transactional
    @Test
    @Order(10)
    public void test_register_and_login_should_be_success() {
        final String username = "ZhAngzhiyan01234";
        final String password = ".!@#-=$%^&*:;aZ0";
        final String description = "0123456789012345678901234567890123456789012345678901234567890123";
        final String nickname = "中文aZ_01234567890";
        String randomCode = RandomUtil.randomString(8);
        AbstractCaptcha captcha = captchaService.generateCaptcha(randomCode);
        String captchaCode = captcha.getCode();

        // 注册
        RegisterParam registerParam = new RegisterParam();
        registerParam.setRandomCode(randomCode);
        registerParam.setCaptchaCode(captchaCode);
        registerParam.setUsername(username);
        registerParam.setPassword(password);
        registerParam.setDescription(description);
        registerParam.setNickName(nickname);
        registerParam.setInviteKey("8a288820");
        ResponseEntity<ResultWrapper> response = restTemplate.postForEntity(
            urlPrefix() + "/register", registerParam, ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getHeaders().get(HttpHeaders.SET_COOKIE).get(0));
        Assertions.assertEquals("/home", String.valueOf(response.getBody().getData()));

        // 登录
        LoginParam loginParam = new LoginParam();
        loginParam.setRandomCode(randomCode);
        loginParam.setCaptchaCode(captchaCode);
        loginParam.setUsername(username);
        loginParam.setPassword(password);
        response = restTemplate.postForEntity(urlPrefix() + "/login", loginParam,
            ResultWrapper.class);
        Map<String, String> cookies = response.getHeaders()
            .getOrEmpty(HttpHeaders.SET_COOKIE)
            .stream()
            .collect(
                Collectors.toMap(cookie -> cookie.split("=")[0], cookie -> cookie.split("=")[1]));

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(cookies.containsKey(UserProperties.USER_ID));
        Assertions.assertTrue(cookies.containsKey(UserProperties.TOKEN_ID));
        Assertions.assertTrue(cookies.containsKey(UserProperties.CSRF_TOKEN));
        Assertions.assertEquals("/home", String.valueOf(response.getBody().getData()));
    }

    @Test
    @Order(1)
    public void test_register_validate_param_failed_should_be_throw_exception() {
        final String username = "ZhAngzhiyan01234(";
        final String password = ".!@#-=$%^&*:;aZ0(";
        final String description = "0123456789012345678901234567890123456789012345678901234567890123(";
        final String nickname = "中文aZ_01234567890(";

        // 注册
        String randomCode = RandomUtil.randomString(8);
        AbstractCaptcha captcha = captchaService.generateCaptcha(randomCode);
        String captchaCode = captcha.getCode();
        RegisterParam registerParam = new RegisterParam();
        registerParam.setRandomCode(randomCode);
        registerParam.setCaptchaCode(captchaCode);
        registerParam.setUsername(username);
        registerParam.setPassword(password);
        registerParam.setDescription(description);
        registerParam.setNickName(nickname);
        registerParam.setInviteKey("8a288820");
        ResponseEntity<ResultWrapper> response = restTemplate.postForEntity(
            urlPrefix() + "/register", registerParam, ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(10)
    public void test_login_should_be_success() {
        final String username = "test";
        final String password = "12345678";
        String randomCode = RandomUtil.randomString(8);
        AbstractCaptcha captcha = captchaService.generateCaptcha(randomCode);
        String captchaCode = captcha.getCode();

        // 登录
        LoginParam loginParam = new LoginParam();
        loginParam.setRandomCode(randomCode);
        loginParam.setCaptchaCode(captchaCode);
        loginParam.setUsername(username);
        loginParam.setPassword(password);
        ResponseEntity<ResultWrapper> response = restTemplate.postForEntity(urlPrefix() + "/login",
            loginParam, ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getHeaders().get(HttpHeaders.SET_COOKIE).get(0));
        Assertions.assertEquals("/home", String.valueOf(response.getBody().getData()));
    }

    @Test
    @Order(11)
    public void test_get_user_info_by_token_should_be_success() {

        // 获取用户信息
        ResponseEntity<ResultWrapper> response = restTemplate.getForEntity(urlPrefix() + "/info",
            ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        UserInfo userInfo = getBean(response, UserInfo.class);
        Assertions.assertEquals(1, userInfo.getUid());
        Assertions.assertEquals("test", userInfo.getUsername());
        Assertions.assertEquals("测试用户", userInfo.getNickName());
        Assertions.assertEquals("https://cdn.jsdelivr.net/gh/IT-JUNKIES/CDN-FILES/img/avatar.png",
            userInfo.getAvatar());
        Assertions.assertEquals("我是测试用户", userInfo.getDescription());
    }

    @Test
    @Order(1)
    public void test_get_user_info_without_token_should_throw_exception() {
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.COOKIE, Collections.emptyList());
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        ResponseEntity<ResultWrapper> response = restTemplate.exchange(urlPrefix() + "/current",
            HttpMethod.GET, requestEntity, ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @Order(11)
    public void test_get_current_user_by_token_should_be_success() {

        // 获取用户信息
        ResponseEntity<ResultWrapper> response = restTemplate.getForEntity(urlPrefix() + "/current",
            ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        CurrentUser currentUser = getBean(response, CurrentUser.class);
        Assertions.assertEquals(1, currentUser.getUid());
        Assertions.assertEquals("test", currentUser.getUsername());
        Assertions.assertEquals("测试用户", currentUser.getNickName());
        Assertions.assertEquals("https://cdn.jsdelivr.net/gh/IT-JUNKIES/CDN-FILES/img/avatar.png",
            currentUser.getAvatar());
        Assertions.assertEquals("我是测试用户", currentUser.getDescription());
        Assertions.assertTrue(!currentUser.getPermissions().isEmpty());
    }

    @Test
    @Order(1)
    public void test_get_current_user_without_token_should_throw_exception() {
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.COOKIE, Collections.emptyList());
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        ResponseEntity<ResultWrapper> response = restTemplate.exchange(urlPrefix() + "/info",
            HttpMethod.GET, requestEntity, ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @Order(1)
    public void test_precheck_nonexistent_username_should_be_success() {
        final String username = "test1";
        userService.precheckUsername(username);
    }

    @Test
    @Order(11)
    public void test_precheck_existent_username_should_throw_exception() {
        final String username = "test";
        Assertions.assertThrows(WebException.class, () -> userService.precheckUsername(username));
    }

    @Test
    public void test_check_permission_should_be_success() {
        Set<String> permissions = Set.of("a.A", "B.b", "C", "a", "b", "c", "e", "f");
        Method doCheckPermission = ReflectUtil.getMethod(UserService.class, "doCheckPermission",
            Set.class, String.class);
        boolean ret = ReflectUtil.invokeStatic(doCheckPermission, permissions,
            "a.A && C || z && e || f && B.b");
        Assertions.assertTrue(ret);
    }

    @Test
    public void test_check_permission_should_be_failed() {
        Set<String> permissions = Set.of("a.A", "B.b", "C", "a", "b", "c", "e", "f");
        Method doCheckPermission = ReflectUtil.getMethod(UserService.class, "doCheckPermission",
            Set.class, String.class);
        boolean ret = ReflectUtil.invokeStatic(doCheckPermission, permissions,
            "a.A && z || z && e || z && B.b");
        Assertions.assertFalse(ret);
    }

    @Test
    @Order(11)
    public void test_get_user_permissions() {

        // 获取用户信息
        ResponseEntity<ResultWrapper> response = restTemplate.getForEntity(
            urlPrefix() + "/resource/permissions", Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        List<String> permissions = (List) response.getBody().getData();
        Assertions.assertEquals(3, permissions.size());
    }

    @Test
    @Order(12)
    public void test_admin_register() {
        final String username = "register";
        final String password = "12345678";
        final String description = "12345678";
        final String nickname = "12345678";
        String randomCode = RandomUtil.randomString(8);
        AbstractCaptcha captcha = captchaService.generateCaptcha(randomCode);
        String captchaCode = captcha.getCode();

        // 注册
        AdminRegisterParam registerParam = new AdminRegisterParam();
        registerParam.setRandomCode(randomCode);
        registerParam.setCaptchaCode(captchaCode);
        registerParam.setUsername(username);
        registerParam.setPassword(password);
        registerParam.setDescription(description);
        registerParam.setNickName(nickname);
        registerParam.setRole("role.normal");
        ResponseEntity<ResultWrapper> response = restTemplate.postForEntity(
            urlPrefix() + "/admin/register", registerParam, ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @Order(12)
    public void test_admin_get_all_roles_should_be_forbidden() {
        ResponseEntity<ResultWrapper> response = restTemplate.getForEntity(
            urlPrefix() + "/resource/admin/roles", ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @Order(100)
    public void test_logout_should_be_success() {
        ResponseEntity<ResultWrapper> response = restTemplate.postForEntity(urlPrefix() + "/logout",
            null, ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseEntity<ResultWrapper> response2 = restTemplate.getForEntity(
            urlPrefix() + "/resource/permissions", Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response2.getStatusCode());
    }

    private String urlPrefix() {
        return URL_PREFIX + port;
    }
}
