package com.gill.user.dto.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * IndividualProfile
 *
 * @author zhangzhiyan
 * @since 2025-03-11
 */
@Getter
@Setter
public class IndividualProfileParam {

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    private String nickName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 首页
     */
    private String home;

    /**
     * 个人简介
     */
    @NotBlank(message = "个人简介不能为空")
    private String description;
}
