package com.gill.user;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import com.gill.user.dto.LoginParam;
import com.gill.user.dto.RegisterParam;
import com.gill.user.dto.UserInfo;
import com.gill.user.service.CaptchaService;
import com.gill.user.service.UserService;
import com.gill.web.api.Response;
import com.gill.web.exception.WebException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

    @Transactional
    @Test
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
        ResponseEntity<Response.ResultWrapper> response = restTemplate.postForEntity(
            urlPrefix() + "/register", registerParam, Response.ResultWrapper.class);
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
            Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getHeaders().get(HttpHeaders.SET_COOKIE).get(0));
        Assertions.assertEquals("/home", String.valueOf(response.getBody().getData()));
    }

    @Test
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
        ResponseEntity<Response.ResultWrapper> response = restTemplate.postForEntity(
            urlPrefix() + "/register", registerParam, Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
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
        ResponseEntity<Response.ResultWrapper> response = restTemplate.postForEntity(
            urlPrefix() + "/login", loginParam, Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getHeaders().get(HttpHeaders.SET_COOKIE).get(0));
        Assertions.assertEquals("/home", String.valueOf(response.getBody().getData()));
    }

    @Test
    public void test_get_user_info_by_token_should_be_success() {

        // 登录
        final String username = "test";
        final String password = "12345678";
        String randomCode = RandomUtil.randomString(8);
        AbstractCaptcha captcha = captchaService.generateCaptcha(randomCode);
        String captchaCode = captcha.getCode();
        LoginParam loginParam = new LoginParam();
        loginParam.setRandomCode(randomCode);
        loginParam.setCaptchaCode(captchaCode);
        loginParam.setUsername(username);
        loginParam.setPassword(password);
        ResponseEntity<Response.ResultWrapper> response1 = restTemplate.postForEntity(
            urlPrefix() + "/login", loginParam, Response.ResultWrapper.class);
        List<String> cookies = response1.getHeaders().get(HttpHeaders.SET_COOKIE);

        // 获取用户信息
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.COOKIE, cookies);
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        ResponseEntity<Response.ResultWrapper> response2 = restTemplate.exchange(
            urlPrefix() + "/info", HttpMethod.GET, requestEntity, Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
        UserInfo userInfo = BeanUtil.mapToBean((Map) response2.getBody().getData(), UserInfo.class,
            true, CopyOptions.create().ignoreError());
        Assertions.assertEquals(0, userInfo.getUid());
        Assertions.assertEquals("test", userInfo.getUsername());
        Assertions.assertEquals("测试用户", userInfo.getNickName());
        Assertions.assertEquals("https://cdn.jsdelivr.net/gh/IT-JUNKIES/CDN-FILES/img/avatar.png",
            userInfo.getAvatar());
        Assertions.assertEquals("我是测试用户", userInfo.getDescription());
    }

    @Test
    public void test_get_user_info_without_token_should_throw_exception() {
        HttpEntity<String> requestEntity = new HttpEntity<>("", new HttpHeaders());
        ResponseEntity<Response.ResultWrapper> response2 = restTemplate.exchange(
            urlPrefix() + "/info", HttpMethod.GET, requestEntity, Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response2.getStatusCode());
    }

    @Test
    public void test_precheck_nonexistent_username_should_be_success() {
        final String username = "test1";
        userService.precheckUsername(username);
    }

    @Test
    public void test_precheck_existent_username_should_throw_exception() {
        final String username = "test";
        Assertions.assertThrows(WebException.class, () -> userService.precheckUsername(username));
    }

    private String urlPrefix() {
        return URL_PREFIX + port;
    }
}
