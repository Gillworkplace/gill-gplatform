package com.gill.notification.worker.core.handler;

import com.gill.notification.worker.core.WebSocketState;
import com.gill.redis.core.Redis;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * DefaultWebSocketHandler
 *
 * @author gill
 * @version 2024/01/30
 **/
@Component
@Order(5)
@Slf4j
public class DefaultWebSocketChildHandler implements WebSocketChildHandler {

    @Autowired
    private Redis redis;

    /**
     * 处理器名字
     *
     * @return 处理器名字
     */
    @Override
    public String name() {
        return "defaultWebSocketHandler";
    }

    /**
     * WebSocket连接建立事件
     *
     * @param context 上下文
     */
    @Override
    public void onOpen(WebSocketHandlerContext context) {
        String uid = context.getUid();
        List<WebSocketHandlerContext> sessions = WebSocketState.USER_SESSIONS.computeIfAbsent(uid,
            key -> new ArrayList<>());
        sessions.add(context);
    }
}
