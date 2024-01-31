package com.gill.notification.worker.core.handler;

import org.springframework.web.socket.CloseStatus;

/**
 * WebSocketHandler
 *
 * @author gill
 * @version 2024/01/30
 **/
public interface WebSocketChildHandler {

    /**
     * 处理器名字
     *
     * @return 处理器名字
     */
    String name();

    /**
     * WebSocket连接建立事件
     *
     * @param context 上下文
     */
    default void onOpen(WebSocketHandlerContext context) throws Exception {
    }

    /**
     * WebSocket 关闭事件
     *
     * @param context     上下文
     * @param closeStatus 关闭原因
     */
    default void onClose(WebSocketHandlerContext context, CloseStatus closeStatus)
        throws Exception {
    }

    /**
     * WebSocket 错误事件
     *
     * @param context   上下文
     * @param exception 异常
     */
    default void onError(WebSocketHandlerContext context, Throwable exception) throws Exception {
    }

    /**
     * WebSocket 消息时间
     *
     * @param context 上下文
     * @param message 消息
     */
    default void onMessage(WebSocketHandlerContext context, String message) throws Exception {
    }
}
