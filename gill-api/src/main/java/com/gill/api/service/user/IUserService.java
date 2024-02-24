package com.gill.api.service.user;

import com.gill.api.domain.UserProperties;

/**
 * UserService
 *
 * @author gill
 * @version 2024/01/31
 **/
public interface IUserService {

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
     */
    void checkToken(Integer uid, String token);

    /**
     * 检查用户权限
     *
     * @param uid                  用户ID
     * @param permissionExpression 权限表达式
     * @param exceptionCode        异常码
     * @param exceptionMessage     异常消息
     */
    void checkPermission(Integer uid, String permissionExpression, int exceptionCode,
        String exceptionMessage);
}
