package com.gill.media.websocket.interceptor;

import com.gill.common.exception.ExceptionUtil;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * WebSocketInterceptors
 *
 * @author gill
 * @version 2024/01/30
 **/
@Component
@Slf4j
public class WebSocketInterceptors implements HandshakeInterceptor {

    @Autowired
    private List<WebSocketInterceptor> interceptors;

    @Override
    public boolean beforeHandshake(@Nonnull ServerHttpRequest request,
        @Nonnull ServerHttpResponse response, @Nonnull WebSocketHandler wsHandler,
        @Nonnull Map<String, Object> attributes) {
        for (WebSocketInterceptor interceptor : interceptors) {
            try {
                if (interceptor.beforeHandshake(request, response, wsHandler, attributes)) {
                    continue;
                }
            } catch (Exception e) {
                log.error("beforeHandshake error, e: {}", ExceptionUtil.getAllMessage(e));
            }
            return false;
        }
        return true;
    }

    @Override
    public void afterHandshake(@Nonnull ServerHttpRequest request,
        @Nonnull ServerHttpResponse response, @Nonnull WebSocketHandler wsHandler,
        Exception exception) {
        for (WebSocketInterceptor interceptor : interceptors) {
            try {
                interceptor.afterHandshake(request, response, wsHandler, exception);
                continue;
            } catch (Exception e) {
                log.error("beforeHandshake error, e: {}", ExceptionUtil.getAllMessage(e));
            }
            return;
        }
    }
}
