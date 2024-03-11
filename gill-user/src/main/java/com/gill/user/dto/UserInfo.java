package com.gill.user.dto;

import com.gill.api.domain.UserProperties;
import com.gill.api.model.User;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * UserInfo
 *
 * @author gill
 * @version 2024/02/13
 **/
@Getter
@Setter
@NoArgsConstructor
public class UserInfo {

    private int uid;

    private String username;

    private String nickName;

    private String avatar;

    private String description;

    private String home;

    public UserInfo(UserInfo user) {
        this.uid = user.getUid();
        this.username = user.getUsername();
        this.nickName = user.getNickName();
        this.avatar = user.getAvatar();
        this.description = user.getDescription();
        this.home = user.getHome();
    }

    public UserInfo(User user) {
        this.uid = user.getId();
        this.username = user.getUsername();
        this.nickName = user.getNickName();
        this.avatar = user.getAvatar();
        this.description = user.getDescription();
        this.home = user.getHome();
    }

    /**
     * 转map
     *
     * @return map
     */
    public Map<String, Object> toMap() {
        Map<String, Object> userInfoMap = new HashMap<>(16);
        userInfoMap.put(UserProperties.USER_ID, String.valueOf(this.getUid()));
        userInfoMap.put(UserProperties.USER_NAME, this.getUsername());
        userInfoMap.put(UserProperties.NICK_NAME, this.getNickName());
        userInfoMap.put(UserProperties.AVATAR, this.getAvatar());
        userInfoMap.put(UserProperties.DESCRIPTION, this.getDescription());
        userInfoMap.put(UserProperties.HOME, this.getHome());
        return userInfoMap;
    }
}
