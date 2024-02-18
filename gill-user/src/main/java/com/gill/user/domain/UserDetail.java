package com.gill.user.domain;

import com.gill.api.model.User;
import java.util.Collections;
import java.util.List;
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

    private String token;

    private int userId;

    private String username;

    private String description;

    private String nickName;

    private String avatar;

    private List<String> permissions = Collections.emptyList();

    public UserDetail(String token, User user) {
        this.token = token;
        this.userId = user.getId();
        this.username = user.getUsername();
        this.description = user.getDescription();
        this.nickName = user.getNickName();
        this.avatar = user.getAvatar();
    }
}
