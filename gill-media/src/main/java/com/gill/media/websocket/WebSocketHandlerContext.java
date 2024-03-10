package com.gill.media.websocket;

import com.gill.api.domain.MediaProperties;
import com.gill.api.domain.UserProperties;
import java.util.Objects;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

/**
 * WebSocketHandlerContext
 *
 * @author gill
 * @version 2024/01/30
 **/
@Getter
public class WebSocketHandlerContext {

    private final WebSocketSession webSocketSession;

    private final String id;

    private final long roomId;

    private final int uid;

    private final long connectTime;

    public WebSocketHandlerContext(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
        this.id = webSocketSession.getId();
        this.roomId = Long.parseLong(
            String.valueOf(webSocketSession.getAttributes().get(MediaProperties.ROOM)));
        this.uid = Integer.parseInt(
            String.valueOf(webSocketSession.getAttributes().get(UserProperties.USER_ID)));
        this.connectTime = System.nanoTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebSocketHandlerContext context = (WebSocketHandlerContext) o;
        return Objects.equals(id, context.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
