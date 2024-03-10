package com.gill.media.webrtc;

import com.gill.media.webrtc.domain.RtcRequest;
import com.gill.media.webrtc.handler.TransferHandler;
import com.gill.media.websocket.WebSocketHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * WebRtcDispatcher
 *
 * @author gill
 * @version 2024/03/06
 **/
@Component
public class WebRtcMeshDispatcher {

    @Autowired
    private TransferHandler transferHandler;

    /**
     * 转发消息
     *
     * @param context 上下文
     * @param message 消息
     */
    public void dispatch(WebSocketHandlerContext context, RtcRequest message) {
        transferHandler.handle(context, message);
    }
}
