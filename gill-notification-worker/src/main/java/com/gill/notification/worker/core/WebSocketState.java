package com.gill.notification.worker.core;

import com.gill.common.util.ServerUtil;
import com.gill.notification.worker.core.handler.WebSocketHandlerContext;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocketState
 *
 * @author gill
 * @version 2024/01/30
 **/
public class WebSocketState {

    /**
     * 服务器ID
     */
    public static final String SERVER_ID = ServerUtil.getRandomServerId("notification-worker");

    /**
     * websocket连接数
     */
    public static final AtomicInteger ONLINE_SESSION_COUNT = new AtomicInteger(0);

    /**
     * 每个用户对应的连接数
     */
    public static final Map<String, List<WebSocketHandlerContext>> USER_SESSIONS = new ConcurrentHashMap<>();
}
