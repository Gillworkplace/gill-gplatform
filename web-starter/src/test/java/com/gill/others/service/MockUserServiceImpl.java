package com.gill.others.service;

import com.gill.api.domain.UserProperties;
import com.gill.api.service.user.IUserService;
import com.gill.api.service.user.UserInfo;
import com.gill.dubbo.contant.Filters;
import com.gill.web.exception.WebException;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.http.HttpStatus;

/**
 * MockUserService
 *
 * @author gill
 * @version 2024/02/22
 **/
@DubboService(filter = Filters.PROVIDER)
public class MockUserServiceImpl implements IUserService {

    public static final int UID = 123456;

    public static final String TID = "abcedf";

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
        if (uid != UID || !TID.equals(token)) {
            throw new WebException(HttpStatus.FORBIDDEN, "未授权登录");
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
        if (uid != UID) {
            throw new WebException(HttpStatus.resolve(exceptionCode), exceptionMessage);
        }
    }
}
