package com.gill.notification.worker;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import com.gill.api.domain.NotificationMessage;
import com.gill.api.domain.NotificationProperties;
import com.gill.notification.worker.service.NotificationService;
import com.gill.redis.core.Redis;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import redis.embedded.RedisServer;

/**
 * WebSocketTest
 *
 * @author gill
 * @version 2024/01/30
 **/
@SpringBootTest(properties = "spring.config.location=classpath:application.yaml", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LongPollingTest {

    @LocalServerPort
    private Integer port;

    private static RedisServer redisServer;

    @Autowired
    private NotificationService service;

    @Autowired
    private Redis redis;

    /**
     * 构造方法之后执行.
     */
    @BeforeAll
    public static void startRedis() throws IOException {
        redisServer = RedisServer.newRedisServer()
            .port(10009)
            .setting("bind 127.0.0.1")
            .setting("maxmemory 128M")
            .build();
        redisServer.start();
    }

    @AfterAll
    public static void stopRedis() throws IOException {
        redisServer.stop();
    }

    @Test
    public void testConnectAndGetMsgs() throws Exception {
        final String gid = InetAddress.getLocalHost().getHostAddress() + "abcd";
        CompletableFuture.runAsync(() -> {
            ThreadUtil.sleep(500L);
            NotificationMessage msg = new NotificationMessage("hello");
            redis.lrpush(NotificationProperties.REDIS_USER_MESSAGES_PREFIX + gid,
                JSONUtil.toJsonStr(msg));
            service.notify(gid);
        });
        OkHttpClient client = new OkHttpClient();
        Request request = new Builder().url(
            "http://localhost:" + port + "/notification/worker/polling?rgid=abcd").get().build();
        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                Assertions.fail("call longPollingMsgs failed");
            }
            ResponseBody body = response.body();
            String data = body.string();
            List<NotificationMessage> res = JSONUtil.toList(data, NotificationMessage.class);
            Assertions.assertEquals(1, res.size());
            Assertions.assertEquals("hello", res.get(0).getMsg());
        } catch (Exception t) {
            Assertions.fail("call longPollingMsgs failed");
        }
    }
}
