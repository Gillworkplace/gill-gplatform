package com.gill.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * LoginParam
 *
 * @author gill
 * @version 2024/02/06
 **/
@Getter
@Setter
public class LoginParam {

    @NotEmpty
    @Schema(description = "参数错误")
    private String randomCode;

    @NotEmpty(message = "验证码不能为空")
    @Schema(description = "验证码")
    private String captchaCode;

    @NotEmpty(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;

    @NotEmpty(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;
}
