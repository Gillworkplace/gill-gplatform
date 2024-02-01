package com.gill.notification.worker.service;

import com.gill.api.notification.IMetricsService;
import com.gill.notification.worker.core.WebSocketState;
import org.springframework.stereotype.Component;

/**
 * WebSocketService
 *
 * @author gill
 * @version 2024/02/01
 **/
@Component
public class WebSocketService implements IMetricsService {

    /**
     * 在线人数统计
     *
     * @return 在线人数
     */
    @Override
    public int onlineCount() {
        return WebSocketState.ONLINE_SESSION_COUNT.get();
    }
}
