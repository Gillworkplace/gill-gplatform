package com.gill.user.mapper;

import com.gill.api.model.Role;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Mapper;

/**
 * PermissionMapper
 *
 * @author gill
 * @version 2024/02/07
 **/
@Mapper
public interface ResourceMapper {

    /**
     * 清空角色
     */
    void truncateRoles();

    /**
     * 清空权限关系
     */
    void truncatePermissions();

    /**
     * 清空角色关系
     */
    void truncateRoleRelationships();

    /**
     * 删除权限关系
     */
    void truncatePermissionRelationships();

    /**
     * 清空角色权限关系
     */
    void truncateRolePermissions();

    /**
     * 根据用户ID 获取角色
     *
     * @param userId 用户ID
     * @return 角色s
     */
    List<Role> queryRolesByUserId(long userId);

    /**
     * 根据用户ID获取权限
     *
     * @param userId 用户ID
     * @return 权限
     */
    Set<String> queryPermissionsByUserId(long userId);
}
