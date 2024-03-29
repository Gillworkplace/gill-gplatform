package com.gill.web.domain;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SwaggerProperties
 *
 * @author gill
 * @version 2024/01/22
 **/
@ConfigurationProperties(prefix = "doc")
@Getter
public class DocProperties {

    /**
     * API文档生成基础路径
     */
    private String apiPathsToMatch;

    /**
     * 是否要启用登录认证
     */
    private boolean enableSecurity;

    /**
     * 文档标题
     */
    private String title;

    /**
     * 文档描述
     */
    private String description;

    /**
     * 文档版本
     */
    private String version;

    /**
     * 文档联系人姓名
     */
    private String contactName;

    /**
     * 文档联系人网址
     */
    private String contactUrl;

    /**
     * 文档联系人邮箱
     */
    private String contactEmail;
}
