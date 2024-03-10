package com.gill.media.webrtc.handler;

import com.gill.common.exception.ExceptionUtil;
import com.gill.media.websocket.WebSocketHandlerContext;
import com.gill.media.websocket.WebSocketState;
import com.gill.media.webrtc.domain.RtcMessage;
import com.gill.media.webrtc.domain.RtcRequest;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

/**
 * TransferHandler
 *
 * @author gill
 * @version 2024/03/06
 **/
@Component
@Slf4j
public class TransferHandler implements RtcHandler {

    /**
     * 处理rtc消息
     *
     * @param context 上下文
     * @param request 消息
     */
    @Override
    public void handle(WebSocketHandlerContext context, RtcRequest request) {
        Optional.ofNullable(WebSocketState.getSocket(context.getRoomId(),
            request.getTo())).ifPresent(session -> {
            try {
                RtcMessage rtcMessage = new RtcMessage(request.getType(), request.getData());
                rtcMessage.setFrom(context.getId());
                session.getSession().sendMessage(new TextMessage(rtcMessage.serialize()));
            } catch (Exception e) {
                log.error("{} send msg to {} failed, msg: {}, e: {}", context.getUid(),
                    session.getUid(), request, ExceptionUtil.getAllMessage(e));
                WebSocketState.clearSession(session.getId());
            }
        });
    }
}
