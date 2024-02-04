package com.gill.api.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * NotificationMessage
 *
 * @author gill
 * @version 2024/02/04
 **/
@Getter
@Setter
public class NotificationMessage {

    private String msg;

    public NotificationMessage(String msg) {
        this.msg = msg;
    }
}
