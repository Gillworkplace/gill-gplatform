package com.gill.api.user;

import com.gill.api.domain.UserProperties;

/**
 * UserService
 *
 * @author gill
 * @version 2024/01/31
 **/
public interface UserService {

    /**
     * 根据userInfo获取用户信息
     *
     * @param token          token id
     * @param userProperties 用户字段
     * @return 用户数据
     */
    UserInfo getUserInfoByToken(String token, UserProperties... userProperties);

    /**
     * 校验token有效性
     *
     * @param uid   用户id
     * @param token tokenid
     * @return 是否有效
     */
    boolean checkToken(String uid, String token);
}
