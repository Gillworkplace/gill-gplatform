package com.gill.media.webrtc.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * RtcMessage
 *
 * @author gill
 * @version 2024/03/06
 **/
@Getter
@Setter
public class RtcRequest {

    private String rtcToken;

    private String to;

    private String type;

    private String data;
}
