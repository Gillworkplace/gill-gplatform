package com.gill.media.websocket.interceptor;

import com.gill.api.domain.MediaProperties;
import com.gill.web.util.RequestUtil;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;

/**
 * UidTidCheckInterceptor
 *
 * @author gill
 * @version 2024/01/31
 **/
@Component
@Order(2)
public class RoomInterceptor implements WebSocketInterceptor {

    @Override
    public boolean beforeHandshake(@Nonnull ServerHttpRequest request,
        @Nonnull ServerHttpResponse response, @Nonnull WebSocketHandler wsHandler,
        @Nonnull Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest httpRequest) {

            // 设置房间号
            HttpServletRequest servletRequest = httpRequest.getServletRequest();
            String room = RequestUtil.resolveName(servletRequest, MediaProperties.ROOM);
            attributes.put(MediaProperties.ROOM, room);
            return true;
        }
        return false;
    }
}
