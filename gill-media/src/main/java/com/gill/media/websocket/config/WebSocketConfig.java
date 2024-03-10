package com.gill.media.websocket.config;

import com.gill.media.websocket.handler.WebSocketHandlers;
import com.gill.media.websocket.interceptor.WebSocketInterceptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocketConfig
 *
 * @author gill
 * @version 2024/01/30
 **/
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private WebSocketHandlers webSocketHandlers;

    @Autowired
    private WebSocketInterceptors webSocketInterceptors;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandlers, "/webrtc/mesh")
            .setAllowedOrigins("*")
            .addInterceptors(webSocketInterceptors);
    }
}
