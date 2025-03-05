package com.gill.api.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 个人简介
     */
    private String description;

    /**
     * 首页地址
     */
    private String home;
}