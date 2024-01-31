package com.gill.notification.worker.mock;

import com.gill.notification.worker.core.handler.WebSocketChildHandler;
import com.gill.notification.worker.core.handler.WebSocketHandlerContext;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * MockServerWebSocketHandler
 *
 * @author gill
 * @version 2024/01/31
 **/
@Component
@Order(999)
public class MockServerWebSocketHandler implements WebSocketChildHandler {

    /**
     * 处理器名字
     *
     * @return 处理器名字
     */
    @Override
    public String name() {
        return "mock";
    }

    /**
     * WebSocket 消息时间
     *
     * @param context 上下文
     * @param message 消息
     */
    @Override
    public void onMessage(WebSocketHandlerContext context, String message) throws Exception {
        WebSocketSession session = context.getWebSocketSession();
        session.sendMessage(new TextMessage(message + " from server"));
    }
}
