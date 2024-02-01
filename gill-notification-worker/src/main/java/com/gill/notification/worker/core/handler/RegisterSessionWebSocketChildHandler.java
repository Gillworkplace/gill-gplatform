package com.gill.notification.worker.core.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.gill.api.domain.NotificationProperties;
import com.gill.api.domain.RedisConstant;
import com.gill.notification.worker.core.WebSocketState;
import com.gill.redis.core.Redis;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;

/**
 * DefaultWebSocketHandler
 *
 * @author gill
 * @version 2024/01/30
 **/
@Component
@Order(5)
@Slf4j
public class RegisterSessionWebSocketChildHandler implements WebSocketChildHandler {

    @Autowired
    private Redis redis;

    /**
     * 处理器名字
     *
     * @return 处理器名字
     */
    @Override
    public String name() {
        return "registerSessionWebSocketHandler";
    }

    /**
     * 用户websocket连接后将该server id添加到该用户下的redis集合中,为用户广播数据提供服务
     *
     * @param context 上下文
     */
    @Override
    public void onOpen(WebSocketHandlerContext context) {
        String uid = context.getUid();
        String redisKey = getRedisKey(uid);
        synchronized (uid.intern()) {
            List<WebSocketHandlerContext> sessions = WebSocketState.USER_SESSIONS.computeIfAbsent(
                uid, key -> new ArrayList<>());

            sessions.add(context);

            // 将该服务器的连接添加至该用户下（用于用户广播数据）
            redis.sadd(redisKey, WebSocketState.SERVER_ID);
        }
    }

    /**
     * 关闭用户websocket连接后检查该worker是否还存有该用户其他的websocket连接 若没有则移除user -> server中 该server id的映射关系
     *
     * @param context     上下文
     * @param closeStatus 关闭原因
     */
    @Override
    public void onClose(WebSocketHandlerContext context, CloseStatus closeStatus) {
        String uid = context.getUid();
        synchronized (uid.intern()) {
            List<WebSocketHandlerContext> sessions = WebSocketState.USER_SESSIONS.get(uid);
            if (sessions != null) {
                sessions.remove(context);
            }
            if (CollectionUtil.isEmpty(sessions)) {
                String redisKey = getRedisKey(uid);
                WebSocketState.USER_SESSIONS.remove(uid);
                redis.sremove(redisKey, WebSocketState.SERVER_ID);
            }
        }
    }

    private static String getRedisKey(String uid) {
        return NotificationProperties.REDIS_USER_LOCATION_PREFIX + uid;
    }
}
