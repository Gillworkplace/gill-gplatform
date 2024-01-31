package com.gill.notification.worker;

import com.gill.api.domain.AuthParamProperties;
import jakarta.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.LoggingWebSocketHandlerDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import redis.embedded.RedisServer;

/**
 * WebSocketTest
 *
 * @author gill
 * @version 2024/01/30
 **/
@SpringBootTest(properties = "spring.config.location=classpath:application.yaml", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest {

    private static final String USER_ID = "user-id";

    private static final String USER_TOKEN = "token-id";

    @LocalServerPort
    private Integer port;

    private static RedisServer redisServer;

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

    private static class MockClientWebSocketHandler extends TextWebSocketHandler {

        private final CompletableFuture<String> future;

        private MockClientWebSocketHandler(CompletableFuture<String> future) {
            this.future = future;
        }

        @Override
        protected void handleTextMessage(@Nonnull WebSocketSession session,
            @Nonnull TextMessage message) {
            future.complete(message.getPayload());
        }
    }

    private static WebSocketClient getWebSocketClient() {
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());
        return new SockJsClient(transports);
    }

    @Test
    public void testAuthFromHeader_success() throws Exception {
        WebSocketClient client = getWebSocketClient();
        CompletableFuture<String> future = new CompletableFuture<>();
        WebSocketHandler webSocketHandler = new LoggingWebSocketHandlerDecorator(
            new MockClientWebSocketHandler(future));
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add(AuthParamProperties.USER_ID.getValue(), USER_ID);
        headers.add(AuthParamProperties.TOKEN_ID.getValue(), USER_TOKEN);
        CompletableFuture<WebSocketSession> sessionCf = client.execute(webSocketHandler, headers,
            new URI("ws://localhost:" + port + "/notification/worker/ws"));
        WebSocketSession session = Assertions.assertDoesNotThrow(
            () -> sessionCf.get(100L, TimeUnit.MILLISECONDS), "connect to server timeout");
        TextMessage msg = new TextMessage("hello");
        session.sendMessage(msg);
        String res = Assertions.assertDoesNotThrow(() -> future.get(1000L, TimeUnit.MILLISECONDS),
            "get response from server timeout");
        Assertions.assertEquals("hello from server", res);
        session.close();
    }

    @Test
    public void testAuthFromParam_success() throws Exception {
        WebSocketClient client = getWebSocketClient();
        CompletableFuture<String> future = new CompletableFuture<>();
        WebSocketHandler webSocketHandler = new LoggingWebSocketHandlerDecorator(
            new MockClientWebSocketHandler(future));
        String queryString = "?" + AuthParamProperties.USER_ID.getValue() + "=" + USER_ID + "&"
            + AuthParamProperties.TOKEN_ID.getValue() + "=" + USER_TOKEN;
        CompletableFuture<WebSocketSession> sessionCf = client.execute(webSocketHandler, null,
            new URI("ws://localhost:" + port + "/notification/worker/ws" + queryString));
        WebSocketSession session = Assertions.assertDoesNotThrow(
            () -> sessionCf.get(100L, TimeUnit.MILLISECONDS), "connect to server timeout");
        TextMessage msg = new TextMessage("hello");
        session.sendMessage(msg);
        String res = Assertions.assertDoesNotThrow(() -> future.get(1000L, TimeUnit.MILLISECONDS),
            "get response from server timeout");
        Assertions.assertEquals("hello from server", res);
        session.close();
    }

    @Test
    public void testAuth_fail() throws Exception {
        WebSocketClient client = getWebSocketClient();
        CompletableFuture<String> future = new CompletableFuture<>();
        WebSocketHandler webSocketHandler = new LoggingWebSocketHandlerDecorator(
            new MockClientWebSocketHandler(future));
        CompletableFuture<WebSocketSession> sessionCf = client.execute(webSocketHandler, null,
            new URI("ws://localhost:" + port + "/notification/worker/ws"));
        Assertions.assertThrows(TimeoutException.class,
            () -> sessionCf.get(100L, TimeUnit.MILLISECONDS), "connect to server timeout");
    }
}
