package com.gill.api.domain;

import lombok.Getter;

/**
 * AuthParamProperties
 *
 * @author gill
 * @version 2024/01/30
 **/
@Getter
public enum AuthParamProperties {

    /**
     * 用户ID
     */
    USER_ID("uid"),

    /**
     * token id
     */
    TOKEN_ID("tid");

    private final String value;

    AuthParamProperties(String value) {
        this.value = value;
    }
}
