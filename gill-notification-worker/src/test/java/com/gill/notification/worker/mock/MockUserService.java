package com.gill.notification.worker.mock;

import com.gill.api.domain.UserProperties;
import com.gill.api.user.UserInfo;
import com.gill.api.user.UserService;
import org.springframework.stereotype.Component;

/**
 * MockUserService
 *
 * @author gill
 * @version 2024/01/31
 **/
@Component
public class MockUserService implements UserService {

    /**
     * 根据userInfo获取用户信息
     *
     * @param token          token id
     * @param userProperties 用户字段
     * @return 用户数据
     */
    @Override
    public UserInfo getUserInfoByToken(String token, UserProperties... userProperties) {
        return null;
    }

    /**
     * 校验token有效性
     *
     * @param uid   用户id
     * @param token tokenid
     * @return 是否有效
     */
    @Override
    public boolean checkToken(String uid, String token) {
        if (uid == null || token == null) {
            return false;
        }
        return uid.startsWith("user-id") && token.startsWith("token-id");
    }
}
