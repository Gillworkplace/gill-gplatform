package com.gill.notification.worker.core;

import com.gill.api.domain.NotificationMessage;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;

/**
 * LongPollingSession
 *
 * @author gill
 * @version 2024/02/02
 **/
@Slf4j
public class LongPollingSession extends Session {

    private final CompletableFuture<List<NotificationMessage>> future = new CompletableFuture<>();

    public LongPollingSession(String id) {
        super(id);
    }

    /**
     * 发送消息
     *
     * @param msgs 消息
     */
    @Override
    public void sendMsgs(List<NotificationMessage> msgs) {
        future.complete(msgs);
    }

    /**
     * 等待获取消息
     *
     * @return 消息
     * @throws Exception 异常
     */
    public List<NotificationMessage> awaitMsgs() throws Exception {
        try {
            return future.get(15, TimeUnit.SECONDS);
        } catch (TimeoutException ignored) {
        }
        return Collections.emptyList();
    }
}
