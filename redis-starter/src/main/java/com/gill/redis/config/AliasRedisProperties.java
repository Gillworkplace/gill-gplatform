package com.gill.redis.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * RedisProperties
 *
 * @author gill
 * @version 2024/01/29
 **/
@Component("aliasRedisProperties")
@ConfigurationProperties(prefix = "redis")
@Getter
@Setter
public class AliasRedisProperties {

    /**
     * 节点集合 {host:localhost}:{port:6379}
     */
    private List<String> nodes;

    private int database = 0;

    private String password;

    /**
     * 解码器命
     */
    private String decryptionName;

    /**
     * 连接池 最大连接数
     */
    private int maxActive = 8;

    /**
     * 最大空闲时间
     */
    private int maxIdle = 8;

    /**
     * 最小连接数
     */
    private int minIdle = 0;

    /**
     * 最大等待时间 ms
     */
    private long maxWait = -1L;
}
