package com.gill.user;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.core.util.RandomUtil;
import com.gill.api.common.SelectorData;
import com.gill.api.model.Role;
import com.gill.user.controller.ResourceController;
import com.gill.user.dto.param.AdminRegisterParam;
import com.gill.user.dto.param.LoginParam;
import com.gill.user.service.CaptchaService;
import com.gill.user.service.UserService;
import com.gill.web.api.Response.ResultWrapper;
import com.gill.web.exception.WebException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.config.location=classpath:application-admin.yaml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminTests extends AbstractTest {

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
    public AdminTests(ResourceController resourceController) {
        super.resourceController = resourceController;
    }

    @Order(0)
    @Test
    public void test_login_admin() {
        final String username = "admin";
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
        Assertions.assertEquals("/admin", String.valueOf(response.getBody().getData()));
    }

    @Test
    @Order(1)
    public void test_admin_get_all_roles() {
        ResponseEntity<ResultWrapper> response = restTemplate.getForEntity(
            urlPrefix() + "/resource/admin/roles", ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        SelectorData<Role> selectorData = getBean(response, SelectorData.class);
        Assertions.assertEquals(2, selectorData.getOptions().size());
    }

    @Test
    @Order(1)
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
        registerParam.setInviteKey("12345678");
        ResponseEntity<ResultWrapper> response = restTemplate.postForEntity(
            urlPrefix() + "/admin/register", registerParam, ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertThrows(WebException.class, () -> userService.precheckUsername("register"));
    }

    private String urlPrefix() {
        return URL_PREFIX + port;
    }
}
