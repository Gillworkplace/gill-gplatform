package com.gill.notification.worker.core.handler;

import com.gill.common.exception.ExceptionUtil;
import jakarta.annotation.Nonnull;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/**
 * WebSocketHandler
 *
 * @author gill
 * @version 2024/01/30
 **/
@Component
@Slf4j
public class WebSocketHandlers extends AbstractWebSocketHandler {

    @Autowired
    private List<WebSocketChildHandler> handlers;

    @Override
    public void afterConnectionEstablished(@Nonnull WebSocketSession session) {
        WebSocketHandlerContext context = new WebSocketHandlerContext(session);
        for (WebSocketChildHandler handler : handlers) {
            try {
                handler.onOpen(context);
            } catch (Exception e) {
                log.error("handler {} on open occur exception, reason: {}", handler.name(),
                    ExceptionUtil.getAllMessage(e));
            }
        }
    }

    @Override
    protected void handleTextMessage(@Nonnull WebSocketSession session,
        @Nonnull TextMessage message) {
        WebSocketHandlerContext context = new WebSocketHandlerContext(session);
        for (WebSocketChildHandler handler : handlers) {
            try {
                handler.onMessage(context, message.getPayload());
            } catch (Exception e) {
                log.error("handler {} on message occur exception, reason: {}", handler.name(),
                    ExceptionUtil.getAllMessage(e));
            }
        }
    }

    @Override
    public void handleTransportError(@Nonnull WebSocketSession session,
        @Nonnull Throwable exception) {
        WebSocketHandlerContext context = new WebSocketHandlerContext(session);
        for (WebSocketChildHandler handler : handlers) {
            try {
                handler.onError(context, exception);
            } catch (Exception e) {
                log.error("handler {} on error occur exception, reason: {}", handler.name(),
                    ExceptionUtil.getAllMessage(e));
            }
        }
    }

    @Override
    public void afterConnectionClosed(@Nonnull WebSocketSession session,
        @Nonnull CloseStatus closeStatus) {
        WebSocketHandlerContext context = new WebSocketHandlerContext(session);
        for (WebSocketChildHandler handler : handlers) {
            try {
                handler.onClose(context, closeStatus);
            } catch (Exception e) {
                log.error("handler {} on close occur exception, reason: {}", handler.name(),
                    ExceptionUtil.getAllMessage(e));
            }
        }
    }
}
