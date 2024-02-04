package com.gill.api.notification;

/**
 * NotificationService
 *
 * @author gill
 * @version 2024/01/29
 **/
public interface INotificationWorkerService {

    /**
     * 通知获取用户的未读消息
     *
     * @param uid 用户id
     * @return 是否通知成功
     */
    boolean notify(String uid);
}
