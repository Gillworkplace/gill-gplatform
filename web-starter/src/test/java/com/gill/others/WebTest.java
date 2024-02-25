package com.gill.others;

import cn.hutool.json.JSONUtil;
import com.gill.others.bean.Body;
import com.gill.others.service.MockUserServiceImpl;
import com.gill.web.api.Response;
import java.util.List;
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

/**
 * WebTest
 *
 * @author gill
 * @version 2024/01/23
 **/
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.config.location=classpath:application-test.yaml")
public class WebTest extends BaseTest {

    public static final String URL_PREFIX = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testAspect_requestParamAndPathVariable_should_return200AndLogMessage() {
        Body body = new Body("key", "value");
        String json = JSONUtil.toJsonStr(body);
        ResponseEntity<String> ret = restTemplate.getForEntity(
            urlPrefix() + "/rest/str/key?key=key&value=value", String.class);
        Assertions.assertEquals(json, ret.getBody());
        ret = restTemplate.getForEntity(urlPrefix() + "/str/key?key=key&value=value", String.class);
        Assertions.assertEquals(json, ret.getBody());
    }

    @Test
    public void testAspect_requestBody_should_return200AndLogMessage() {
        Body body = new Body("key", "value");
        String json = JSONUtil.toJsonStr(body);
        ResponseEntity<Response.ResultWrapper> ret = restTemplate.postForEntity(
            urlPrefix() + "/rest/res", body, Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.OK, ret.getStatusCode());
        Assertions.assertEquals(json, JSONUtil.toJsonStr(ret.getBody().getData()));
        ret = restTemplate.postForEntity(urlPrefix() + "/res", body, Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.OK, ret.getStatusCode());
        Assertions.assertEquals(json, JSONUtil.toJsonStr(ret.getBody().getData()));
    }

    @Test
    public void testAspect_ex_should_return500AndLogMessage() {
        ResponseEntity<?> ret = restTemplate.getForEntity(urlPrefix() + "/rest/ex",
            Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ret.getStatusCode());
        ret = restTemplate.getForEntity(urlPrefix() + "/ex", Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ret.getStatusCode());
    }

    @Test
    public void testAspect_webEx_should_return400AndLogMessage() {
        ResponseEntity<Response.ResultWrapper> ret = restTemplate.getForEntity(
            urlPrefix() + "/rest/webEx", Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ret.getStatusCode());
        Assertions.assertEquals("failed", ret.getBody().getMessage());
        ret = restTemplate.getForEntity(urlPrefix() + "/webEx", Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ret.getStatusCode());
        Assertions.assertEquals("failed", ret.getBody().getMessage());
    }

    @Test
    public void testWebLogAspect_webEx_should_returnAndLogMessage() {
        ResponseEntity<?> ret = restTemplate.getForEntity(urlPrefix() + "/rest/webEx",
            Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ret.getStatusCode());
        ret = restTemplate.getForEntity(urlPrefix() + "/webEx", Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ret.getStatusCode());
    }

    @Test
    public void testAspect_validEx_should_returnAndLogMessage() {
        ResponseEntity<?> ret = restTemplate.getForEntity(urlPrefix() + "/rest/validEx?number=-1",
            Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ret.getStatusCode());
        ret = restTemplate.getForEntity(urlPrefix() + "/validEx?number=-1",
            Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ret.getStatusCode());
    }

    @Test
    public void test_interceptor_auth_should_be_access() {
        List<String> cookies = List.of("uid=" + MockUserServiceImpl.UID,
            "tid=" + MockUserServiceImpl.TID);
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.COOKIE, cookies);
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        ResponseEntity<Response.ResultWrapper> response = restTemplate.exchange(
            urlPrefix() + "/rest/auth", HttpMethod.GET, requestEntity,
            Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void test_interceptor_auth_should_be_deny() {
        List<String> cookies = List.of("uid=0", "tid=" + MockUserServiceImpl.TID);
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.COOKIE, cookies);
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        ResponseEntity<Response.ResultWrapper> response = restTemplate.exchange(urlPrefix() + "/rest/auth",
            HttpMethod.GET, requestEntity, Response.ResultWrapper.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    private String urlPrefix() {
        return URL_PREFIX + port;
    }
}
