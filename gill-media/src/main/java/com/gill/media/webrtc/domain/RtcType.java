package com.gill.media.webrtc.domain;

import lombok.Getter;

/**
 * RtcType
 *
 * @author gill
 * @version 2024/03/07
 **/
@Getter
public enum RtcType {

    /**
     * 接收连接
     */
    ACCEPT_CONNECTION("accept_connection");

    private final String name;

    RtcType(String name) {
        this.name = name;
    }
}
