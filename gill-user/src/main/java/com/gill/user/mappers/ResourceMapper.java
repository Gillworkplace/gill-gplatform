package com.gill.user.mappers;

import com.gill.api.model.Permission;
import com.gill.api.model.PermissionRelationship;
import com.gill.api.model.Role;
import com.gill.api.model.RolePermission;
import com.gill.api.model.RoleRelationship;
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

    /**
     * 根据用户ID 获取角色
     *
     * @param userId 用户ID
     * @return 角色s
     */
    List<Role> queryRolesByUserId(int userId);

    /**
     * 根据用户ID获取权限
     *
     * @param userId 用户ID
     * @return 权限
     */
    Set<String> queryPermissionsByUserId(int userId);

    /**
     * 根据用户ID 获取详细权限信息
     *
     * @param userId 用户ID
     * @return 权限
     */
    List<Permission> queryDetailPermissionsByUserId(int userId);

    /**
     * 为用户添加角色
     *
     * @param userId 用户ID
     * @param roles  角色
     */
    void insertUserRoles(int userId, Set<String> roles);

    /**
     * 为用户删除角色
     *
     * @param userId 用户ID
     * @param roles  角色
     */
    void deleteUserRoles(int userId, Set<String> roles);
}
