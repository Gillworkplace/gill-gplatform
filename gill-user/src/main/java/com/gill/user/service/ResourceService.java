package com.gill.user.service;

import com.gill.api.domain.UserProperties;
import com.gill.api.model.Role;
import com.gill.redis.core.Redis;
import com.gill.user.domain.Permissions;
import com.gill.user.domain.Relation;
import com.gill.user.domain.Roles;
import com.gill.user.entity.PermissionEntity;
import com.gill.user.entity.PermissionRelationshipsEntity;
import com.gill.user.entity.RoleEntity;
import com.gill.user.entity.RolePermissionsEntity;
import com.gill.user.entity.RoleRelationshipsEntity;
import com.gill.user.entity.UserRolesEntity;
import com.gill.user.mapper.ResourceMapper;
import com.gill.user.service.mapperservice.PermissionRelationshipsService;
import com.gill.user.service.mapperservice.PermissionService;
import com.gill.user.service.mapperservice.RolePermissionsService;
import com.gill.user.service.mapperservice.RoleRelationshipsService;
import com.gill.user.service.mapperservice.RoleService;
import com.gill.user.service.mapperservice.UserRolesService;
import com.gill.web.exception.WebException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ResourceService
 *
 * @author gill
 * @version 2024/02/07
 **/
@Component
@Slf4j
public class ResourceService {

    private static final long EXPIRED_TIME = 7L * 24 * 3600 * 1000;

    @Autowired
    private Redis redis;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private UserRolesService userRolesService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRelationshipsService roleRelationshipsService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PermissionRelationshipsService permissionRelationshipService;

    @Autowired
    private RolePermissionsService rolePermissionService;

    /**
     * 解析并插入角色与权限数据
     *
     * @param roles       角色数据
     * @param permissions 权限数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void parseAndInsertIntoDb(Roles roles, Permissions permissions) {

        // 删除旧数据
        resourceMapper.truncateRoles();
        resourceMapper.truncateRoleRelationships();
        resourceMapper.truncatePermissions();
        resourceMapper.truncatePermissionRelationships();
        resourceMapper.truncateRolePermissions();

        // 插入角色数据
        List<RoleEntity> newRoles = roles.getRoles();
        roleService.saveBatch(newRoles);

        // 分析并插入角色关系数据
        List<RoleRelationshipsEntity> newRoleRelations = resolveRoleRelations(
            roles.getRoleRelations());
        roleRelationshipsService.saveBatch(newRoleRelations);

        // 插入权限数据
        List<PermissionEntity> newPermissions = permissions.getPermissions();
        permissionService.saveBatch(newPermissions);

        // 分析并插入权限关系数据
        List<PermissionRelationshipsEntity> newPermissionRelations = resolvePermissionRelations(
            permissions.getPermissions(), permissions.getRelations());
        permissionRelationshipService.saveBatch(newPermissionRelations);

        // 插入角色权限数据
        List<RolePermissionsEntity> newRolePermissions = resolveRolePermissions(
            roles.getRoleRelations(), roles.getRolePermissions(), permissions.getPermissions());
        rolePermissionService.saveBatch(newRolePermissions);
    }

    private static Map<String, Set<String>> toAdjacencyMap(List<Relation> roleRelations) {
        Map<String, Set<String>> adjacencyMap = new HashMap<>();
        for (Relation relation : roleRelations) {
            String roleId = relation.getSubject();
            Set<String> children = adjacencyMap.computeIfAbsent(roleId, k -> new HashSet<>());
            children.addAll(relation.getItems());
        }
        return adjacencyMap;
    }

    private boolean checkCircle(Map<String, Set<String>> adjacencyMap) {
        Set<String> visited = new HashSet<>();
        Set<String> circle = new HashSet<>();
        for (String subject : adjacencyMap.keySet()) {
            if (doCheckCircle(visited, circle, subject, adjacencyMap)) {
                return true;
            }
        }
        return false;
    }

    private boolean doCheckCircle(Set<String> visited, Set<String> circle, String subject,
        Map<String, Set<String>> adjacencyMap) {
        if (circle.contains(subject)) {
            return true;
        }
        if (visited.contains(subject)) {
            return false;
        }
        circle.add(subject);
        Set<String> children = adjacencyMap.getOrDefault(subject, Collections.emptySet());
        for (String child : children) {
            if (doCheckCircle(visited, circle, child, adjacencyMap)) {
                return true;
            }
        }
        visited.add(subject);
        circle.remove(subject);
        return false;
    }

    private List<RoleRelationshipsEntity> resolveRoleRelations(List<Relation> roleRelations) {
        Map<String, Set<String>> adjacencyMap = toAdjacencyMap(roleRelations);
        if (checkCircle(adjacencyMap)) {
            throw new WebException(HttpStatus.BAD_REQUEST, "角色发现环状关系");
        }
        List<RoleRelationshipsEntity> roleRelationships = new ArrayList<>();
        for (Entry<String, Set<String>> entry : adjacencyMap.entrySet()) {
            String roleId = entry.getKey();
            for (String child : entry.getValue()) {
                RoleRelationshipsEntity relationship = new RoleRelationshipsEntity();
                relationship.setRoleId(roleId);
                relationship.setChildId(child);
                roleRelationships.add(relationship);
            }
        }
        return roleRelationships;
    }

    private List<PermissionRelationshipsEntity> resolvePermissionRelations(
        List<PermissionEntity> permissions, List<Relation> relations) {
        Map<String, Set<String>> adjacencyMap = toAdjacencyMap(relations);
        if (checkCircle(adjacencyMap)) {
            throw new WebException(HttpStatus.BAD_REQUEST, "权限发现环状关系");
        }
        List<PermissionRelationshipsEntity> permissionRelationships = new ArrayList<>();

        // self
        for (PermissionEntity permission : permissions) {
            PermissionRelationshipsEntity self = new PermissionRelationshipsEntity();
            self.setAncestorId(permission.getId());
            self.setDescendantId(permission.getId());
            self.setAdjoin(RelationType.SELF.getCode());
            permissionRelationships.add(self);
        }

        // 上下级关系
        Map<String, Set<String>> cache = new HashMap<>();
        for (Entry<String, Set<String>> entry : adjacencyMap.entrySet()) {
            String ancestor = entry.getKey();
            dfsBuildPermissionRelations(adjacencyMap, ancestor, permissionRelationships, cache);
        }
        return permissionRelationships;
    }

    private Set<String> dfsBuildPermissionRelations(Map<String, Set<String>> adjacencyMap,
        String ancestor, List<PermissionRelationshipsEntity> permissionRelations,
        Map<String, Set<String>> cache) {
        Set<String> descendants = cache.get(ancestor);
        if (descendants != null) {
            return descendants;
        }

        // dfs 获取子孙权限集合
        Set<String> children = adjacencyMap.getOrDefault(ancestor, Collections.emptySet());
        descendants = new HashSet<>(children);
        for (String child : children) {
            Set<String> childDescendants = dfsBuildPermissionRelations(adjacencyMap, child,
                permissionRelations, cache);
            descendants.addAll(childDescendants);
        }

        // 子孙权限添加至列表
        for (String descendant : descendants) {
            int adjoin = children.contains(descendant) ? RelationType.CHILD.getCode()
                : RelationType.DESCENDANT.getCode();
            PermissionRelationshipsEntity relationship = new PermissionRelationshipsEntity();
            relationship.setAncestorId(ancestor);
            relationship.setDescendantId(descendant);
            relationship.setAdjoin(adjoin);
            permissionRelations.add(relationship);
        }
        cache.put(ancestor, descendants);
        return descendants;
    }


    private List<RolePermissionsEntity> resolveRolePermissions(List<Relation> roleRelations,
        List<Relation> rolePermissions, List<PermissionEntity> permissions) {
        List<RolePermissionsEntity> rps = new ArrayList<>();
        Map<String, Set<String>> roleAdjMap = toAdjacencyMap(roleRelations);
        Map<String, Set<String>> roleAdjPermissionsMap = toAdjacencyMap(rolePermissions);
        Set<String> permissionIds = permissions.stream()
            .map(PermissionEntity::getId)
            .collect(Collectors.toSet());

        Map<String, Set<String>> cache = new HashMap<>(256);
        for (String roleId : roleAdjMap.keySet()) {
            dfsFindRoleDescendants(roleAdjMap, permissionIds, roleId, rps, roleAdjPermissionsMap,
                cache);
        }
        return rps;
    }

    private Set<String> dfsFindRoleDescendants(Map<String, Set<String>> adjacencyMap,
        Set<String> permissionIds, String subject, List<RolePermissionsEntity> rps,
        Map<String, Set<String>> roleAdjPermissions, Map<String, Set<String>> cache) {
        Set<String> descendants = cache.get(subject);
        if (descendants != null) {
            return descendants;
        }
        Set<String> selfPermissions = roleAdjPermissions.getOrDefault(subject,
            Collections.emptySet());
        Set<String> children = adjacencyMap.getOrDefault(subject, Collections.emptySet());
        descendants = new HashSet<>(children);
        for (String child : children) {
            Set<String> childDescendants = dfsFindRoleDescendants(adjacencyMap, permissionIds,
                child, rps, roleAdjPermissions, cache);
            descendants.addAll(childDescendants);
        }
        Set<String> permissions = descendants.stream()
            .map(descendant -> roleAdjPermissions.getOrDefault(descendant, Collections.emptySet()))
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
        permissions.addAll(selfPermissions);
        for (String permission : permissions) {

            // 忽略关系中没有定义的权限
            if (!permissionIds.contains(permission)) {
                continue;
            }
            int self = selfPermissions.contains(permission) ? RelationType.CHILD.getCode()
                : RelationType.DESCENDANT.getCode();
            RolePermissionsEntity rp = new RolePermissionsEntity();
            rp.setRoleId(subject);
            rp.setPermissionId(permission);
            rp.setSelf(self);
            rps.add(rp);
        }

        cache.put(subject, descendants);
        return descendants;
    }

    /**
     * 根据用户id获取所有的资源权限
     *
     * @param userId 用户ID
     * @return set 权限集合
     */
    public Set<String> refreshUserPermissions(long userId) {
        Set<String> permissions = resourceMapper.queryPermissionsByUserId(userId);
        redis.clear(UserProperties.getRedisUserResourceKey(userId));
        redis.sadd(UserProperties.getRedisUserResourceKey(userId), permissions, EXPIRED_TIME);
        return permissions;
    }

    /**
     * 根据用户ID 获取所有资源权限
     *
     * @param userId 用户ID
     * @return set 权限集合
     */
    public Set<String> getUserPermissions(long userId) {
        Set<String> permissions = redis.sget(UserProperties.getRedisUserResourceKey(userId));
        if (permissions.isEmpty()) {
            return refreshUserPermissions(userId);
        }
        return permissions;
    }

    /**
     * 为用户添加角色
     *
     * @param userId 用户ID
     * @param roles  角色
     */
    public void addUserRoles(long userId, Set<String> roles) {
        List<UserRolesEntity> entities = new ArrayList<>();
        for (String role : roles) {
            UserRolesEntity entity = new UserRolesEntity();
            entity.setUserId(userId);
            entity.setRoleId(role);
            entities.add(entity);
        }
        userRolesService.saveBatch(entities);
    }

    /**
     * 为用户删除角色
     *
     * @param userId 用户ID
     * @param roles  角色
     */
    public void removeUserRoles(long userId, Set<String> roles) {
        Set<Integer> ids = userRolesService.lambdaQuery()
            .select(UserRolesEntity::getId)
            .eq(UserRolesEntity::getUserId, userId)
            .in(UserRolesEntity::getRoleId, roles)
            .isNull(UserRolesEntity::getDeleteTime)
            .list()
            .stream()
            .map(UserRolesEntity::getId)
            .collect(Collectors.toSet());

        userRolesService.lambdaUpdate()
            .set(UserRolesEntity::getDeleteTime, LocalDateTime.now())
            .set(UserRolesEntity::getDeleted, true)
            .in(UserRolesEntity::getId, ids)
            .update();
    }

    /**
     * 判断角色是否存在
     *
     * @param role 角色ID
     * @return boolean
     */
    public boolean containsRole(String role) {
        return roleService.lambdaQuery().eq(RoleEntity::getId, role).exists();
    }

    /**
     * 获取所有角色
     *
     * @return 所有角色
     */
    public List<Role> queryAllRoles() {
        List<RoleEntity> entities = roleService.list();
        return entities.stream().map(entity -> {
            Role role = new Role();
            role.setId(entity.getId());
            role.setName(entity.getName());
            role.setDescription(entity.getDescription());
            return role;
        }).collect(Collectors.toList());
    }

    @Getter
    public enum RelationType {

        /**
         * 自己
         */
        SELF(0),

        /**
         * 孩子节点
         */
        CHILD(1),

        /**
         * 孙节点
         */
        DESCENDANT(2);

        private final int code;

        RelationType(int code) {
            this.code = code;
        }
    }
}
