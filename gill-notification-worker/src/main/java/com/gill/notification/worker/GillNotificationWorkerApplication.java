package com.gill.notification.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * GillNotificationWorkerApplication
 *
 * @author gill
 * @version 2024/01/29
 **/
@SpringBootApplication
public class GillNotificationWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GillNotificationWorkerApplication.class, args);
    }
}
