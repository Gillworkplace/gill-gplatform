package com.gill.media.webrtc.domain;

import java.util.Set;
import lombok.Getter;

/**
 * RtcPermission
 *
 * @author gill
 * @version 2024/03/07
 **/
@Getter
public class RtcPermission {

    private final String rtcToken;

    private final Set<String> socketIds;

    public RtcPermission(String rtcToken, Set<String> socketIds) {
        this.rtcToken = rtcToken;
        this.socketIds = socketIds;
    }
}
