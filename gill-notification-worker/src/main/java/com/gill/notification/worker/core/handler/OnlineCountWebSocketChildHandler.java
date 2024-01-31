package com.gill.notification.worker.core.handler;

import com.gill.notification.worker.core.WebSocketState;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;

/**
 * OnlineCountWebSocketHandler
 *
 * @author gill
 * @version 2024/01/30
 **/
@Component
@Order(2)
public class OnlineCountWebSocketChildHandler implements WebSocketChildHandler {

    /**
     * 处理器名字
     *
     * @return 处理器名字
     */
    @Override
    public String name() {
        return "onlineCountWebSocketHandler";
    }

    /**
     * WebSocket连接建立事件
     *
     * @param context 上下文
     */
    @Override
    public void onOpen(WebSocketHandlerContext context) {
        WebSocketState.ONLINE_SESSION_COUNT.incrementAndGet();
    }

    /**
     * WebSocket 关闭事件
     *
     * @param context     上下文
     * @param closeStatus 关闭原因
     */
    @Override
    public void onClose(WebSocketHandlerContext context, CloseStatus closeStatus) {
        WebSocketState.ONLINE_SESSION_COUNT.decrementAndGet();
    }
}
