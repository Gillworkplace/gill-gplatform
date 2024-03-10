package com.gill.media.webrtc.domain;

import cn.hutool.json.JSONUtil;
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
public class RtcMessage {

    private final String type;

    private final String data;

    private String from;

    public RtcMessage(String type, Object data) {
        this.type = type;
        if(data instanceof String) {
            this.data = (String) data;
        } else {
            this.data = JSONUtil.toJsonStr(data);
        }
    }

    /**
     * 序列化
     *
     * @return 结果
     */
    public String serialize() {
        return JSONUtil.toJsonStr(this);
    }
}
