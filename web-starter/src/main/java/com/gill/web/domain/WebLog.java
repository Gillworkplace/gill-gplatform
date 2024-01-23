package com.gill.web.domain;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * WebLog
 *
 * @author gill
 * @version 2024/01/22
 **/
@Getter
@Setter
public class WebLog {

    /**
     * 处理是否成功
     */
    private boolean success;

    /**
     * 操作用户
     */
    private String username;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 操作时间
     */
    private Long startTime;

    /**
     * 消耗时间
     */
    private Long cost;

    /**
     * 根路径
     */
    private String hostPath;

    /**
     * URI
     */
    private String uri;

    /**
     * URL
     */
    private String url;

    /**
     * 请求类型
     */
    private String method;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 请求参数
     */
    private List<Object> parameter;

    /**
     * 返回结果
     */
    private Object result;
}
