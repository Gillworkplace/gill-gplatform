package com.gill.user.mappers;

import com.gill.api.model.Permission;
import com.gill.api.model.PermissionRelationship;
import com.gill.api.model.RolePermission;
import com.gill.api.model.RoleRelationship;
import com.gill.user.domain.Roles;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * ResourceTestMapper
 *
 * @author gill
 * @version 2024/02/08
 **/
@Mapper
public interface ResourceTestMapper {

    List<Roles> queryAllRoles();

    List<Permission> queryAllPermissions();

    List<RoleRelationship> queryAllRoleRelationships();

    List<PermissionRelationship> queryAllPermissionRelationships();

    List<RolePermission> queryAllRolePermissions();
}
