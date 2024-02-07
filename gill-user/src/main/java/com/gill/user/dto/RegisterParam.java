package com.gill.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * RegisterParam
 *
 * @author gill
 * @version 2024/02/06
 **/
@Getter
@Setter
public class RegisterParam {

    @Pattern(regexp = "^\\w{8}$", message = "参数错误")
    @NotNull(message = "参数错误")
    private String randomCode;

    @Pattern(regexp = "^[0-9a-zA-Z]{1,8}$", message = "参数错误")
    @NotNull(message = "验证码错误")
    private String captchaCode;

    @Pattern(regexp = "^[0-9a-zA-Z]{6,16}$", message = "用户名长度应为6-16位且只能由数字、大小写英文字母组成")
    @NotNull(message = "用户名不能为空")
    private String username;

    @Pattern(regexp = "^[0-9a-zA-Z.!@#-=$%^&*:;]{6,16}$", message = "密码长度应为6-16位且只能由0-9,a-z,A-Z以及.!@#-=$%^&*:;组成")
    @NotNull(message = "密码不能为空")
    private String password;

    @Pattern(regexp = "^[\\w\\u4e00-\\u9fa5]{1,16}$", message = "昵称长度应为1-16位且只能由数字、大小写英文字母、\"_\"以及中文组成")
    @NotNull(message = "昵称不能为空")
    private String nickName;

    @Pattern(regexp = "[/0-9a-zA-Z]{0,64}", message = "参数错误")
    private String avatar;

    @Length(max = 64, message = "个人描述字数应小于等于64个")
    private String description;
}
