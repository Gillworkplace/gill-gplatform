package com.gill.user.domain;

import com.gill.api.model.User;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * UserDetail
 *
 * @author gill
 * @version 2024/02/13
 **/
@Getter
@Setter
public class UserDetail {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * token
     */
    private String token;

    /**
     * 用户名
     */
    private String username;

    /**
     * 个人简介
     */
    private String description;

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
     * 权限
     */
    private Collection<String> permissions = Collections.emptyList();

    public UserDetail(String token, User user, Set<String> permissions) {
        this.userId = user.getId();
        this.token = token;
        this.username = user.getUsername();
        this.description = user.getDescription();
        this.nickName = user.getNickName();
        this.avatar = user.getAvatar();
        this.home = user.getHome();
        this.permissions = permissions;
    }
}
