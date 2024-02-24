package com.gill.dubbo.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ProtocolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * DubboConfiguration
 *
 * @author gill
 * @version 2024/02/23
 **/
@Slf4j
public class DubboConfiguration {

    @Bean
    public ProtocolConfig protocolConfig(@Value("${dubbo.protocol.name:dubbo}") String protocolName,
        @Value("${dubbo.protocol.port:-1}") Integer port) {
        log.info("preset default protocol config");
        ProtocolConfig config = new ProtocolConfig();
        config.setName(protocolName);
        config.setPort(port);
        return config;
    }
}
