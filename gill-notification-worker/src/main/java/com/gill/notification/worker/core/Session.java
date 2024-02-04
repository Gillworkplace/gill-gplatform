package com.gill.notification.worker.core;

import com.gill.api.domain.NotificationMessage;
import java.util.List;
import lombok.Getter;

/**
 * Session
 *
 * @author gill
 * @version 2024/02/02
 **/
@Getter
public abstract class Session {

    protected final String id;

    public Session(String id) {
        this.id = id;
    }

    /**
     * 发送消息
     *
     * @param msgs 消息
     */
    public abstract void sendMsgs(List<NotificationMessage> msgs);
}
