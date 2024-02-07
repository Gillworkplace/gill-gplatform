package com.gill.user;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.core.util.RandomUtil;
import com.gill.user.dto.LoginParam;
import com.gill.user.dto.RegisterParam;
import com.gill.user.service.CaptchaService;
import com.gill.web.api.Response;
import com.google.common.net.HttpHeaders;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import redis.embedded.RedisServer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GillUserApplicationTests {

    public static final String URL_PREFIX = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CaptchaService captchaService;

    private static RedisServer redisServer;

    /**
     * 构造方法之后执行.
     */
    @BeforeAll
    public static void startRedis() throws Exception {
        redisServer = RedisServer.newRedisServer()
            .port(19000)
            .setting("bind 127.0.0.1")
            .setting("maxmemory 128M")
            .setting("requirepass 123456")
            .build();
        redisServer.start();
    }

    @AfterAll
    public static void stopRedis() throws Exception {
        redisServer.stop();
    }

    @Transactional
    @Test
    public void test_register_and_login_should_be_success() {
        final String username = "ZhAngzhiyan01234";
        final String password = ".!@#-=$%^&*:;aZ0";
        final String description = "0123456789012345678901234567890123456789012345678901234567890123";
        final String nickname = "中文aZ_01234567890";

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

    private String urlPrefix() {
        return URL_PREFIX + port;
    }
}
