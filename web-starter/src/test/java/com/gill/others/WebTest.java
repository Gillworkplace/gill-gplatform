package com.gill.others;

import com.gill.others.bean.Body;
import com.gill.web.api.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

/**
 * WebTest
 *
 * @author gill
 * @version 2024/01/23
 **/
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebTest {

    public static final String URL_PREFIX = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testWebLogAspect_requestParamAndPathVariable_should_returnAndLogMessage() {
        String ret = restTemplate.getForObject(urlPrefix() + "/rest/str/key?value=value",
            String.class);
        Assertions.assertEquals("key=value", ret);
        ret = restTemplate.getForObject(urlPrefix() + "/str/key?value=value", String.class);
        Assertions.assertEquals("key=value", ret);
    }

    @Test
    public void testWebLogAspect_requestBody_should_returnAndLogMessage() {
        Result<?> ret = restTemplate.postForObject(urlPrefix() + "/rest/res",
            new Body("key", "value"), Result.class);
        Assertions.assertEquals(HttpStatus.OK.value(), ret.getCode());
        ret = restTemplate.postForObject(urlPrefix() + "/res", new Body("key", "value"),
            Result.class);
        Assertions.assertEquals(HttpStatus.OK.value(), ret.getCode());
    }

    @Test
    public void testWebLogAspect_noParam_should_returnAndLogMessage() {
        Result<?> ret = restTemplate.getForObject(urlPrefix() + "/rest/noParam", Result.class);
        Assertions.assertEquals("", ret.getData());
        ret = restTemplate.getForObject(urlPrefix() + "/noParam", Result.class);
        Assertions.assertEquals("", ret.getData());
    }

    @Test
    public void testWebLogAspect_ex_should_returnAndLogMessage() {
        Result<?> ret = restTemplate.getForObject(urlPrefix() + "/rest/ex", Result.class);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), ret.getCode());
        ret = restTemplate.getForObject(urlPrefix() + "/ex", Result.class);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), ret.getCode());
    }

    @Test
    public void testWebLogAspect_webEx_should_returnAndLogMessage() {
        Result<?> ret = restTemplate.getForObject(urlPrefix() + "/rest/webEx", Result.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), ret.getCode());
        ret = restTemplate.getForObject(urlPrefix() + "/webEx", Result.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), ret.getCode());
    }

    @Test
    public void testWebLogAspect_validEx_should_returnAndLogMessage() {
        Result<?> ret = restTemplate.getForObject(urlPrefix() + "/rest/validEx?number=-1",
            Result.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), ret.getCode());
        ret = restTemplate.getForObject(urlPrefix() + "/validEx?number=-1", Result.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), ret.getCode());
    }

    private String urlPrefix() {
        return URL_PREFIX + port;
    }
}
