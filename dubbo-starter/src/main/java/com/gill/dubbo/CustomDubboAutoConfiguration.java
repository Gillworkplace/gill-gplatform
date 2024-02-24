package com.gill.dubbo;

import com.gill.dubbo.config.DubboConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.dubbo.spring.boot.autoconfigure.DubboAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Import;

/**
 * DubboAutoConfiguration
 *
 * @author gill
 * @version 2024/02/23
 **/
@AutoConfigureBefore({DubboAutoConfiguration.class})
@Slf4j
@EnableDubbo(scanBasePackages = "com.gill")
@Import(DubboConfiguration.class)
public class CustomDubboAutoConfiguration {

    @PostConstruct
    private void init() {
        log.info("dubbo auto configuration action");
    }
}
