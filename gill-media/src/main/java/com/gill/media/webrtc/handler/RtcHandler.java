package com.gill.media.webrtc.handler;

import com.gill.media.websocket.WebSocketHandlerContext;
import com.gill.media.webrtc.domain.RtcRequest;
import java.io.IOException;

/**
 * RtcHandler
 *
 * @author gill
 * @version 2024/03/06
 **/
public interface RtcHandler {

    /**
     * 处理rtc消息
     *
     * @param context 上下文
     * @param message 消息
     */
    void handle(WebSocketHandlerContext context, RtcRequest message) throws IOException;
}
