package com.gill.media.websocket;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

/**
 * WebsocketSession
 *
 * @author gill
 * @version 2024/02/02
 **/
@Getter
@Slf4j
public class WebsocketSession {

    private final String id;

    private final WebSocketSession session;

    private final int uid;

    public WebsocketSession(int uid, WebSocketSession session) {
        this.id = session.getId();
        this.uid = uid;
        this.session = session;
    }
}
