package com.gill.notification.worker.mock;

import com.gill.api.domain.UserProperties;
import com.gill.api.service.user.IUserService;
import com.gill.api.service.user.UserInfo;
import com.gill.web.exception.WebException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * MockUserService
 *
 * @author gill
 * @version 2024/01/31
 **/
@Component
public class MockUserService implements IUserService {

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
     */
    @Override
    public void checkToken(Integer uid, String token) {
        if (uid == null || token == null) {
            throw new WebException(HttpStatus.UNAUTHORIZED, "un auth");
        }
        if (uid != 0 || !token.startsWith("token-id")) {
            throw new WebException(HttpStatus.UNAUTHORIZED, "un auth");
        }
    }

    /**
     * 检查用户权限
     *
     * @param uid                  用户ID
     * @param permissionExpression 权限表达式
     * @param exceptionCode        异常码
     * @param exceptionMessage     异常消息
     */
    @Override
    public void checkPermission(Integer uid, String permissionExpression, int exceptionCode,
        String exceptionMessage) {

    }
}
