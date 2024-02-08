package com.gill.user.mappers;

import com.gill.api.model.Permission;
import com.gill.api.model.PermissionRelationship;
import com.gill.api.model.Role;
import com.gill.api.model.RolePermission;
import com.gill.api.model.RoleRelationship;
import java.util.List;
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
     * 插入角色
     *
     * @param roles 角色
     */
    void insertRoles(List<Role> roles);

    /**
     * 清空角色
     */
    void truncateRoles();

    /**
     * 插入权限
     *
     * @param permissions 权限
     */
    void insertPermissions(List<Permission> permissions);

    /**
     * 清空权限关系
     */
    void truncatePermissions();


    /**
     * 插入角色关系
     *
     * @param relations 关系
     */
    void insertRoleRelationships(List<RoleRelationship> relations);

    /**
     * 清空角色关系
     */
    void truncateRoleRelationships();

    /**
     * 插入权限关系
     *
     * @param relations 关系
     */
    void insertPermissionRelationships(List<PermissionRelationship> relations);

    /**
     * 删除权限关系
     */
    void truncatePermissionRelationships();

    /**
     * 添加角色权限关系
     *
     * @param relations 关系
     */
    void insertRolePermissions(List<RolePermission> relations);

    /**
     * 清空角色权限关系
     */
    void truncateRolePermissions();

}
