package com.gill.user.dto;

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
public class IndividualProfile {

    /**
     * 用户ID
     */
    private String uid;

    /**
     * 账号名
     */
    private String username;

    /**
     * 昵称
     */
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
    private String description;

    /**
     * 创建时间
     */
    private Long createTime;
}
