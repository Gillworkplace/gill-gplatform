package com.gill.user.service;

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
}
