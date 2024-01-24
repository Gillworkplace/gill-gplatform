package com.gill.web;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * WebAutoConfiguration
 *
 * @author gill
 * @version 2024/01/24
 **/
@ComponentScan(basePackages = "com.gill.web")
@AutoConfiguration
@Slf4j
public class WebAutoConfiguration {

    @PostConstruct
    private void init() {
        log.info("web auto configuration action");
    }
}
