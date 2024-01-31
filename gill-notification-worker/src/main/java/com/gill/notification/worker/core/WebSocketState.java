package com.gill.notification.worker.core;

import com.gill.notification.worker.core.handler.WebSocketHandlerContext;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocketState
 *
 * @author gill
 * @version 2024/01/30
 **/
public class WebSocketState {

    /**
     * websocket连接数
     */
    public static final AtomicInteger ONLINE_SESSION_COUNT = new AtomicInteger(0);

    /**
     * 每个用户对应的连接数
     */
    public static final Map<String, List<WebSocketHandlerContext>> USER_SESSIONS = new ConcurrentHashMap<>();

    /**
     * 每个用户对应的socketId位置
     */
    public static final Map<String, AtomicInteger> USER_SOCKET_IDS = new ConcurrentHashMap<>();

    /**
     * 获取用户的下一个socketId
     *
     * @param userId 用户ID
     * @return socketId
     */
    public static int generateSocketId(String userId) {
        return USER_SOCKET_IDS.computeIfAbsent(userId, key -> new AtomicInteger(0))
            .incrementAndGet();
    }
}
