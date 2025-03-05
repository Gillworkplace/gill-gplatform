package com.gill.generator.config;

import java.util.Collections;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "db")
@Data
public class GeneratorConfig {

    /**
     * 数据库连接
     */
    private String url;

    /**
     * 数据库用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * basePackage
     */
    private String basePackage;

    /**
     * 模块
     */
    private String module;

    /**
     * 表
     */
    private List<String> tables = Collections.emptyList();
}
