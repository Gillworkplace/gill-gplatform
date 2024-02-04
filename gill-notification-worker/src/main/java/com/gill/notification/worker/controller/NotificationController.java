package com.gill.notification.worker.controller;

import com.gill.api.domain.NotificationMessage;
import com.gill.common.exception.ExceptionUtil;
import com.gill.notification.worker.core.LongPollingSession;
import com.gill.notification.worker.core.SessionState;
import com.gill.notification.worker.service.SessionService;
import com.gill.web.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * NotificationController
 *
 * @author gill
 * @version 2024/01/30
 **/
@RestController
@Slf4j
public class NotificationController {

    @Autowired
    private SessionService sessionService;

    /**
     * 长连接获取推送消息
     *
     * @return 消息列表
     */
    @GetMapping("/polling")
    public List<NotificationMessage> longPollingMsgs(@RequestParam("rgid") String rgid,
        HttpServletRequest request) {
        String gid = RequestUtil.getRequestIp(request) + rgid;
        String sid = String.valueOf(SessionState.EPHEMERAL_SESSION_ID_ALLOCATOR.incrementAndGet());
        try {
            LongPollingSession session = new LongPollingSession(sid);
            sessionService.saveSession(gid, session);
            return session.awaitMsgs();
        } catch (Exception e) {
            log.error("long polling msgs occur exception: {}", ExceptionUtil.getAllMessage(e));
        } finally {
            sessionService.removeSession(gid, sid);
        }
        return Collections.emptyList();
    }
}
