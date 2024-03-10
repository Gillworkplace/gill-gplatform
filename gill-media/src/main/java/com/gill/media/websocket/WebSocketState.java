package com.gill.media.websocket;

import cn.hutool.core.util.RandomUtil;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebRtcMeshState
 *
 * @author gill
 * @version 2024/03/06
 **/
public class WebSocketState {

    private static final AtomicInteger ONLINE_SESSION_COUNT = new AtomicInteger(0);

    private static final Map<Long, Map<String, WebsocketSession>> GROUP_SESSIONS = new ConcurrentHashMap<>();

    private static final Map<String, WebsocketSession> SESSION_MAP = new ConcurrentHashMap<>();

    private static final Map<Long, String> roomTokens = new ConcurrentHashMap<>();

    /**
     * 添加session
     *
     * @param roomId  房间号
     * @param session 会话信息
     * @return 其他连接列表
     */
    public static Set<String> offerSession(long roomId, WebsocketSession session) {
        Map<String, WebsocketSession> roomMap = GROUP_SESSIONS.computeIfAbsent(roomId,
            key -> new ConcurrentHashMap<>(16));
        SESSION_MAP.put(session.getId(), session);
        Set<String> sockets;
        synchronized (roomMap) {
            ONLINE_SESSION_COUNT.incrementAndGet();
            sockets = new HashSet<>(roomMap.keySet());
            roomMap.put(session.getId(), session);
        }
        return sockets;
    }

    /**
     * 清除会话消息
     *
     * @param sessionId 消息
     */
    public static void clearSession(String sessionId) {
        Optional.ofNullable(SESSION_MAP.get(sessionId)).ifPresent(socket -> {
            try {
                socket.getSession().close();
            } catch (Exception ignored) {
            }
        });
        SESSION_MAP.remove(sessionId);
        for (Map<String, WebsocketSession> roomMap : GROUP_SESSIONS.values()) {
            roomMap.remove(sessionId);
            ONLINE_SESSION_COUNT.decrementAndGet();
        }
    }


    /**
     * 获取房间内所有socket
     *
     * @param roomId    房间
     * @param sessionId 会话id
     * @return socket
     */
    public static WebsocketSession getSocket(long roomId, String sessionId) {
        return GROUP_SESSIONS.getOrDefault(roomId, Collections.emptyMap()).get(sessionId);
    }

    /**
     * 获取房间token
     *
     * @param roomId 房间号
     * @return token
     */
    public static String getRoomToken(long roomId) {
        return roomTokens.computeIfAbsent(roomId, key -> RandomUtil.randomString(16));
    }

    /**
     * 校验房间token
     *
     * @param roomId   房间号
     * @param rtcToken token
     * @return 是否成功
     */
    public static boolean checkRoomToken(long roomId, String rtcToken) {
        return rtcToken.equals(roomTokens.get(roomId));
    }
}
