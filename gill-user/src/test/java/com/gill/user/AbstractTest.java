package com.gill.user;

import cn.hutool.json.JSONUtil;
import com.gill.user.controller.ResourceController;
import com.gill.web.api.Response.ResultWrapper;
import org.apache.curator.test.TestingServer;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
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

    private static TestingServer zkServer;

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

        zkServer = new TestingServer(2181, true); // 2181 是 Zookeeper 的默认端口
        zkServer.start();
    }

    @AfterAll
    public static void stopRedis() throws Exception {
        redisServer.stop();

        // 先关dubbo再关zk，否则dubbo会进行zk重连导致要很久才能退出
        DubboBootstrap.getInstance().destroy();
        zkServer.stop();
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
