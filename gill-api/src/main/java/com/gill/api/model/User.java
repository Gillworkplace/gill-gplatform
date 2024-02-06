package com.gill.api.model;

import com.gill.api.domain.UserProperties;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {

    private Integer id;

    private String username;

    private String encryptPassword;

    private String salt;

    private Date createTime;

    private Date loginTime;

    private String nickName;

    private String avatar;

    private String description;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getEncryptPassword() {
        return encryptPassword;
    }

    public void setEncryptPassword(String encryptPassword) {
        this.encryptPassword = encryptPassword == null ? null : encryptPassword.trim();
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt == null ? null : salt.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar == null ? null : avatar.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    /**
     * 转换成redis的map信息
     *
     * @return map
     */
    public Map<String, Object> toRedisUserInfoMap() {
        Map<String, Object> userInfoMap = new HashMap<>(16);
        userInfoMap.put(UserProperties.USER_ID, String.valueOf(this.id));
        userInfoMap.put(UserProperties.USER_NAME, String.valueOf(this.username));
        userInfoMap.put(UserProperties.NICK_NAME, String.valueOf(this.nickName));
        userInfoMap.put(UserProperties.AVATAR, String.valueOf(this.avatar));
        userInfoMap.put(UserProperties.DESCRIPTION, String.valueOf(this.description));
        return userInfoMap;
    }
}