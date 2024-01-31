package com.gill.api.domain;

import lombok.Getter;

/**
 * TokenProperties
 *
 * @author gill
 * @version 2024/01/30
 **/
@Getter
public enum UserProperties {

    /**
     * 用户ID
     */
    USER_ID("uid");

    private final String value;

    UserProperties(String value) {
        this.value = value;
    }
}
