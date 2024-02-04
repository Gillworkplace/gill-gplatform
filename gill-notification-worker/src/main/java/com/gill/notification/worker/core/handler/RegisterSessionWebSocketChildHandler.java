package com.gill.notification.worker.core.handler;

import com.gill.notification.worker.core.WebsocketSession;
import com.gill.notification.worker.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

/**
 * DefaultWebSocketHandler
 *
 * @author gill
 * @version 2024/01/30
 **/
@Component
@Order(5)
@Slf4j
public class RegisterSessionWebSocketChildHandler implements WebSocketChildHandler {

    @Autowired
    private SessionService sessionService;

    /**
     * 处理器名字
     *
     * @return 处理器名字
     */
    @Override
    public String name() {
        return "registerSessionWebSocketHandler";
    }

    /**
     * 用户websocket连接后将该server id添加到该用户下的redis集合中,为用户广播数据提供服务
     *
     * @param context 上下文
     */
    @Override
    public void onOpen(WebSocketHandlerContext context) {
        String uid = context.getUid();
        WebsocketSession session = new WebsocketSession(uid, context.getWebSocketSession());
        sessionService.saveSession(uid, session);
    }

    /**
     * 关闭用户websocket连接后检查该worker是否还存有该用户其他的websocket连接 若没有则移除user -> server中 该server id的映射关系
     *
     * @param context     上下文
     * @param closeStatus 关闭原因
     */
    @Override
    public void onClose(WebSocketHandlerContext context, CloseStatus closeStatus) {
        String uid = context.getUid();
        WebSocketSession session = context.getWebSocketSession();
        sessionService.removeSession(uid, session.getId());
    }
}
