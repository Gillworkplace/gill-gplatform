package com.gill.media.websocket.handler.sub;

import com.gill.common.exception.ExceptionUtil;
import com.gill.media.websocket.WebSocketHandlerContext;
import com.gill.media.websocket.handler.WebSocketChildHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;

/**
 * LogWebSocketHandler
 *
 * @author gill
 * @version 2024/01/30
 **/
@Component
@Order(1)
@Slf4j
public class LoggingWebSocketChildHandler implements WebSocketChildHandler {

    /**
     * 处理器名字
     *
     * @return 处理器名字
     */
    @Override
    public String name() {
        return "logWebSocketHandler";
    }

    /**
     * WebSocket连接建立事件
     *
     * @param context 上下文
     */
    @Override
    public void onOpen(WebSocketHandlerContext context) {
        log.debug("websocket connect to {}", context.getUid());
    }

    /**
     * WebSocket 关闭事件
     *
     * @param context     上下文
     * @param closeStatus 关闭原因
     */
    @Override
    public void onClose(WebSocketHandlerContext context, CloseStatus closeStatus) {
        String reason = closeStatus.getReason() == null ? "" : closeStatus.getReason();
        log.debug("websocket disconnect to {}, reason：{} {}", context.getUid(),
            closeStatus.getCode(), reason);
    }

    /**
     * WebSocket 错误事件
     *
     * @param context   上下文
     * @param exception 异常
     */
    @Override
    public void onError(WebSocketHandlerContext context, Throwable exception) {
        log.error("websocket error occur at {}, reason：{}", context.getUid(),
            ExceptionUtil.getAllMessage(exception));
    }

    /**
     * WebSocket 消息时间
     *
     * @param context 上下文
     * @param message 消息
     */
    @Override
    public void onMessage(WebSocketHandlerContext context, TextMessage message) {
        log.debug("receive {} from {}", message.toString(), context.getUid());
    }
}
