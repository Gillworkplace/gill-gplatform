package com.gill.api.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 权限
 *
 * @author zhagnzhiyan
 * @since 2025/03/04
 */
@Getter
@Setter
public class Permission {

    /**
     * id
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 说明
     */
    private String description;
}