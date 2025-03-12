package com.gill.user.service;

import com.gill.user.dto.IndividualProfile;
import com.gill.user.dto.param.IndividualProfileParam;

/**
 * IUserService
 *
 * @author zhangzhiyan
 * @since 2025-03-05
 */
public interface IUserService {

    /**
     * 获取邀请码
     *
     * @param userId 用户ID
     * @return 邀请码
     */
    String getInviteKey(long userId);

    /**
     * 刷新邀请码
     *
     * @param userId 用户ID
     */
    void refreshInviteKey(long userId);

    /**
     * 获取个人资料
     *
     * @param userId 用户ID
     * @return 个人资料
     */
    IndividualProfile getProfile(long userId);

    /**
     * 修改个人资料
     *
     * @param userId 用户ID
     * @param params 参数
     */
    void updateProfile(long userId, IndividualProfileParam params);
}
