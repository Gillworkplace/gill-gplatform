package com.gill.notification.worker.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * NotificationMessage
 *
 * @author gill
 * @version 2024/05/04
 **/
@Getter
@Setter
@ToString
public class NotificationMessage {

    /**
     * 消息路由
     */
    private String router;

    /**
     * 发送者
     */
    private String sender;

    /**
     * 消息数据
     */
    private Object data;
}
