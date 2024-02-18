package com.gill.user.config;

import java.util.Set;

/**
 * DefaultRole
 *
 * @author gill
 * @version 2024/02/18
 **/
public class RoleMap {

    /**
     * 管理员
     */
    public static final Set<String> ADMIN = Set.of("role.admin");

    /**
     * 普通用户
     */
    public static final Set<String> NORMAL_USER = Set.of("role.normal");

}
