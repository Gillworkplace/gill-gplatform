package com.gill.user.controller;

import com.gill.common.util.ObjectUtil;
import com.gill.user.domain.Permissions;
import com.gill.user.domain.Roles;
import com.gill.user.service.ResourceService;
import com.gill.web.annotation.OperationPermission;
import com.gill.web.api.Response;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ResourceController
 *
 * @author gill
 * @version 2024/02/08
 **/
@RequestMapping("/resource")
@RestController
public class ResourceController {

    private static final String ROLES_YAML = "roles.yaml";

    private static final String PERMISSION_YAML = "permission.yaml";

    @Autowired
    private ResourceService resourceService;

    /**
     * 加载资源配置
     */
    @OperationPermission(permissionExpression = "permission.admin")
    @PostMapping("/load")
    public void loadResources() {

        // 读取角色、权限配置文件
        Roles roles = ObjectUtil.readYamlFromClasspath(ROLES_YAML, Roles.class);
        Permissions permissions = ObjectUtil.readYamlFromClasspath(PERMISSION_YAML,
            Permissions.class);

        // 角色、权限入库
        resourceService.parseAndInsertIntoDb(roles, permissions);
    }

    /**
     * 获取用户权限
     *
     * @param userId 用户ID
     * @return 授权信息
     */
    @GetMapping("/permissions")
    public Response<Set<String>> userPermission(@CookieValue("uid") int userId) {
        Set<String> permissions = resourceService.getUserPermissions(userId);
        return Response.success(permissions).build();
    }
}
