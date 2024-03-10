package com.gill.media.websocket.interceptor;

import jakarta.annotation.Nonnull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * WebSocketInterceptor
 *
 * @author gill
 * @version 2024/01/31
 **/
public interface WebSocketInterceptor extends HandshakeInterceptor {

    default void afterHandshake(@Nonnull ServerHttpRequest request,
        @Nonnull ServerHttpResponse response, @Nonnull WebSocketHandler wsHandler,
        Exception exception) {

    }

}
