package com.gill.user;

import cn.hutool.json.JSONUtil;
import com.gill.user.controller.ResourceController;
import com.gill.web.api.Response.ResultWrapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.ResponseEntity;
import redis.embedded.RedisServer;

/**
 * AbstractTest
 *
 * @author gill
 * @version 2024/02/18
 **/
public class AbstractTest {

    protected static RedisServer redisServer;

    protected ResourceController resourceController;

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


    @BeforeEach
    public void beforeEach() {
        if (resourceController != null) {
            resourceController.loadResources();
        }
    }

    protected <T> T getBean(ResponseEntity<ResultWrapper> response, Class<T> clazz) {
        return JSONUtil.toBean(JSONUtil.toJsonStr(response.getBody().getData()), clazz);
    }
}
