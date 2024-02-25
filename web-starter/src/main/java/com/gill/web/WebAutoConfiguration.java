package com.gill.web;

import com.gill.web.config.DocConfig;
import com.gill.web.config.InterceptorConfig;
import com.gill.web.config.MvcConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * WebAutoConfiguration
 *
 * @author gill
 * @version 2024/01/24
 **/
@ComponentScan(basePackages = {"com.gill.web"})
@Slf4j
public class WebAutoConfiguration {

    @PostConstruct
    private void init() {
        log.info("web auto configuration action");
    }
}
