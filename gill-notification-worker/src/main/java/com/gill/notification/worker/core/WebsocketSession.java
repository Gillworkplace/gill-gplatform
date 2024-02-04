package com.gill.notification.worker.core;

import com.gill.api.domain.NotificationMessage;
import com.gill.common.exception.ExceptionUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * WebsocketSession
 *
 * @author gill
 * @version 2024/02/02
 **/
@Slf4j
public class WebsocketSession extends Session {

    private final WebSocketSession session;

    private final String uid;

    public WebsocketSession(String uid, WebSocketSession session) {
        super(session.getId());
        this.uid = uid;
        this.session = session;
    }

    /**
     * 发送消息
     *
     * @param msgs 消息
     */
    @Override
    public void sendMsgs(List<NotificationMessage> msgs) {
        try {
            for (NotificationMessage msg : msgs) {
                session.sendMessage(new TextMessage(msg.getMsg()));
            }
        } catch (Exception e) {
            log.error("send message to uid {} session {} failed, e: {}", uid, id,
                ExceptionUtil.getAllMessage(e));
        }
    }
}
