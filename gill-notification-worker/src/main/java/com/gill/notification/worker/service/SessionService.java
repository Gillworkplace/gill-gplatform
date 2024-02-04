package com.gill.notification.worker.service;

import com.gill.notification.worker.core.Session;
import com.gill.notification.worker.core.SessionState;
import com.gill.notification.worker.util.CommonUtil;
import com.gill.redis.core.Redis;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * SessionService
 *
 * @author gill
 * @version 2024/02/04
 **/
@Component
@Slf4j
public class SessionService {

    private static final int PRE_ALLOCATED_CAPACITY = 16;

    @Autowired
    private Redis redis;

    /**
     * 保存会话连接
     *
     * @param gid     组ID
     * @param session 会话连接
     */
    public void saveSession(String gid, Session session) {
        String redisKey = CommonUtil.getRedisKey(gid);
        synchronized (gid.intern()) {
            Map<String, Session> sessions = SessionState.GROUP_SESSIONS.computeIfAbsent(gid,
                key -> new HashMap<>(PRE_ALLOCATED_CAPACITY));
            sessions.put(session.getId(), session);

            // 将该服务器的连接添加至该组下（用于广播数据）
            redis.sadd(redisKey, SessionState.SERVER_ID);
        }
    }


    /**
     * 移除会话连接
     *
     * @param gid 组id
     * @param sid 会话连接id
     */
    public void removeSession(String gid, String sid) {
        String redisKey = CommonUtil.getRedisKey(gid);
        synchronized (gid.intern()) {
            Map<String, Session> sessions = SessionState.GROUP_SESSIONS.get(gid);
            if (sessions != null) {
                sessions.remove(sid);
            }

            // 将该服务器的连接从该组下移除（用于广播数据）
            redis.sremove(redisKey, SessionState.SERVER_ID);
        }
    }
}
