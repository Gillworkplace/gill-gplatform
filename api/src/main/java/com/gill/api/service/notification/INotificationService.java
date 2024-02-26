package com.gill.api.service.notification;

import java.util.List;

/**
 * NotificationService
 *
 * @author gill
 * @version 2024/01/29
 **/
public interface INotificationService {

    /**
     * 给某一个连接发送消息
     *
     * @param socketId 连接id
     * @param message  消息内容
     */
    void sendMsg(String socketId, String message);

    /**
     * 批量给某一个连接发送消息
     *
     * @param socketId 连接ID
     * @param messages 消息内容
     */
    void sendMsgs(String socketId, List<String> messages);

    /**
     * 给某一些连接发送消息
     *
     * @param socketIds 连接集合
     * @param message   消息
     */
    void batchSendMsg(List<String> socketIds, String message);

    /**
     * 给某一些连接发送消息
     *
     * @param socketIds 连接集合
     * @param messages  消息
     */
    void batchSendMsgs(List<String> socketIds, List<String> messages);
}
