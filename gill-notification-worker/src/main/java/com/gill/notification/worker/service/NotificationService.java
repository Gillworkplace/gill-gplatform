package com.gill.notification.worker.service;

import cn.hutool.core.collection.CollectionUtil;
import com.gill.api.domain.NotificationMessage;
import com.gill.api.domain.NotificationProperties;
import com.gill.api.notification.IMetricsService;
import com.gill.api.notification.INotificationWorkerService;
import com.gill.common.api.DLock;
import com.gill.common.exception.ExceptionUtil;
import com.gill.notification.worker.core.Session;
import com.gill.notification.worker.core.SessionState;
import com.gill.redis.core.Redis;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * WebSocketService
 *
 * @author gill
 * @version 2024/02/01
 **/
@Component
@Slf4j
public class NotificationService implements IMetricsService, INotificationWorkerService {

    private static final int BATCH = 100;

    @Autowired
    private DLock lock;

    @Autowired
    private Redis redis;

    /**
     * 在线人数统计
     *
     * @return 在线人数
     */
    @Override
    public int onlineCount() {
        return SessionState.ONLINE_SESSION_COUNT.get();
    }


    /**
     * 通知获取用户的未读消息
     *
     * @param gid 会话组id
     * @return 是否通知成功
     */
    @Override
    public boolean notify(String gid) {
        Map<String, Session> sessions = SessionState.GROUP_SESSIONS.get(gid);
        if (CollectionUtil.isEmpty(sessions)) {
            return false;
        }
        String lockKey = NotificationProperties.REDIS_USER_MSG_LOCK_PREFIX + gid;
        lock.lock(lockKey);
        try {
            String msgKey = NotificationProperties.REDIS_USER_MESSAGES_PREFIX + gid;
            List<NotificationMessage> msgs;

            // 分批发送
            while (true) {
                msgs = redis.lrange(msgKey, 0, BATCH, NotificationMessage.class);
                if (CollectionUtil.isEmpty(msgs)) {
                    break;
                }
                for (Session session : sessions.values()) {
                    session.sendMsgs(msgs);
                }
                redis.lpop(msgKey, BATCH);
            }
            return true;
        } catch (Exception e) {
            log.error("notify gid {} failed, e: {}", gid, ExceptionUtil.getAllMessage(e));
            return false;
        } finally {
            lock.unlock(lockKey);
        }
    }
}
