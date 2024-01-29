package com.gill.oauth.thirdpart.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * AccessTokenRes
 *
 * @author gill
 * @version 2024/01/19
 **/
@Getter
@Setter
@ToString
public class Token {

    @NotBlank
    private String openUserId;

    @NotBlank
    private String accessToken;

    @NotBlank
    private String refreshToken;
}
