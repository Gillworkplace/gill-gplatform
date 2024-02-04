package com.gill.notification.worker.service;

import com.gill.notification.worker.core.LongPollingSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * LongPollingService
 *
 * @author gill
 * @version 2024/02/04
 **/
@Component
@Slf4j
public class LongPollingService {

    @Autowired
    private SessionService sessionService;

    /**
     * 注册长轮询会话
     *
     * @param gid 组id
     * @param sid 会话id
     */
    public LongPollingSession registerEphemeralSession(String gid, String sid) {
        LongPollingSession session = new LongPollingSession(sid);
        sessionService.saveSession(gid, session);
        return session;
    }

    /**
     * 注销长轮询会话
     *
     * @param gid 组id
     * @param sid 会话id
     */
    public void unregisterEphemeralSession(String gid, String sid) {
        sessionService.removeSession(gid, sid);
    }
}
