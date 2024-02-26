package com.gill.oss.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OssConfig
 *
 * @author gill
 * @version 2024/01/15
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = "oss")
public class OssProperty {

    private String bucket;

    private String publicResourcePath;
}
