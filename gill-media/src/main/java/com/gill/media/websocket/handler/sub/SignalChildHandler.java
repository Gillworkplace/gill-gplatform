package com.gill.media.websocket.handler.sub;

import cn.hutool.json.JSONUtil;
import com.gill.common.exception.ExceptionUtil;
import com.gill.media.websocket.WebsocketSession;
import com.gill.media.websocket.WebSocketHandlerContext;
import com.gill.media.webrtc.WebRtcMeshDispatcher;
import com.gill.media.websocket.WebSocketState;
import com.gill.media.webrtc.domain.RtcMessage;
import com.gill.media.webrtc.domain.RtcPermission;
import com.gill.media.webrtc.domain.RtcRequest;
import com.gill.media.webrtc.domain.RtcType;
import com.gill.media.websocket.handler.WebSocketChildHandler;
import java.io.IOException;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * WebRtcChildHandler
 *
 * @author gill
 * @version 2024/03/06
 **/
@Component
@Slf4j
public class SignalChildHandler implements WebSocketChildHandler {

    @Autowired
    private WebRtcMeshDispatcher dispatcher;

    /**
     * 处理器名字
     *
     * @return 处理器名字
     */
    @Override
    public String name() {
        return "signal handler";
    }

    /**
     * WebSocket连接建立事件
     *
     * @param context 上下文
     */
    @Override
    public void onOpen(WebSocketHandlerContext context) {
        log.debug("accept websocket uid: {}, sessionId: {}", context.getUid(), context.getId());

        // 获取连接token
        String rtcToken = generateRtcToken(context);

        // 登记 连接
        Set<String> socketIds = registerSocket(context);

        // 返回 当前房间连接列表 与 token
        try {
            RtcPermission rtcPermission = new RtcPermission(rtcToken, socketIds);
            RtcMessage rtcMessage = new RtcMessage(RtcType.ACCEPT_CONNECTION.getName(),
                rtcPermission);

            WebSocketSession socket = context.getWebSocketSession();
            socket.sendMessage(new TextMessage(rtcMessage.serialize()));
        } catch (IOException e) {
            log.error("send accept connection to uid: {}, session id {}failed, reason: {}",
                context.getUid(), context.getId(), ExceptionUtil.getAllMessage(e));
        }
    }

    private static String generateRtcToken(WebSocketHandlerContext context) {
        long roomId = context.getRoomId();
        return WebSocketState.getRoomToken(roomId);
    }

    private static Set<String> registerSocket(WebSocketHandlerContext context) {
        WebsocketSession session = new WebsocketSession(context.getUid(),
            context.getWebSocketSession());
        return WebSocketState.offerSession(context.getRoomId(), session);
    }

    /**
     * WebSocket 关闭事件
     *
     * @param context     上下文
     * @param closeStatus 关闭原因
     */
    @Override
    public void onClose(WebSocketHandlerContext context, CloseStatus closeStatus) {
        log.debug("close websocket {} {}", context.getUid(), context.getId());
        WebSocketState.clearSession(context.getId());
    }

    /**
     * WebSocket 错误事件
     *
     * @param context   上下文
     * @param exception 异常
     */
    @Override
    public void onError(WebSocketHandlerContext context, Throwable exception) {
        log.debug("close websocket {} {}, reason: {}", context.getUid(), context.getId(),
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
        RtcRequest rtcRequest = JSONUtil.toBean(message.getPayload(), RtcRequest.class);
        if (WebSocketState.checkRoomToken(context.getRoomId(), rtcRequest.getRtcToken())) {
            dispatcher.dispatch(context, rtcRequest);
        } else {
            log.error("uid: {}, session: {} send unauth message: {}", context.getUid(),
                context.getId(), message.getPayload());
        }
    }
}
