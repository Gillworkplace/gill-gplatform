package com.gill.user.service;

import com.gill.api.model.Permission;
import com.gill.api.model.PermissionRelationship;
import com.gill.api.model.Role;
import com.gill.api.model.RolePermission;
import com.gill.api.model.RoleRelationship;
import com.gill.user.domain.Permissions;
import com.gill.user.domain.Relation;
import com.gill.user.domain.Roles;
import com.gill.user.mappers.ResourceMapper;
import com.gill.web.exception.WebException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
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

    @Autowired
    private ResourceMapper resourceMapper;

    @Transactional(rollbackFor = Exception.class)
    public void parseAndInsertIntoDb(Roles roles, Permissions permissions) {

        // 删除旧数据
        resourceMapper.truncateRoles();
        resourceMapper.truncateRoleRelationships();
        resourceMapper.truncatePermissions();
        resourceMapper.truncatePermissionRelationships();
        resourceMapper.truncateRolePermissions();

        // 插入角色数据
        List<Role> newRoles = roles.getRoles();
        resourceMapper.insertRoles(newRoles);

        // 分析并插入角色关系数据
        List<RoleRelationship> newRoleRelations = analyseRoleRelations(roles.getRoleRelations());
        resourceMapper.insertRoleRelationships(newRoleRelations);

        // 插入权限数据
        List<Permission> newPermissions = permissions.getPermissions();
        resourceMapper.insertPermissions(newPermissions);

        // 分析并插入权限关系数据
        List<PermissionRelationship> newPermissionRelations = analysePermissionRelations(
            permissions.getPermissions(), permissions.getRelations());
        resourceMapper.insertPermissionRelationships(newPermissionRelations);

        // 插入角色权限数据
        List<RolePermission> newRolePermissions = analyseRolePermissions(roles.getRoleRelations(),
            roles.getRolePermissions(), permissions.getPermissions());
        resourceMapper.insertRolePermissions(newRolePermissions);
    }

    private static Map<String, Set<String>> transferToAdjacencyMap(List<Relation> roleRelations) {
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

    private List<RoleRelationship> analyseRoleRelations(List<Relation> roleRelations) {
        Map<String, Set<String>> adjacencyMap = transferToAdjacencyMap(roleRelations);
        if (checkCircle(adjacencyMap)) {
            throw new WebException(HttpStatus.BAD_REQUEST, "角色发现环状关系");
        }
        List<RoleRelationship> roleRelationships = new ArrayList<>();
        for (Entry<String, Set<String>> entry : adjacencyMap.entrySet()) {
            String roleId = entry.getKey();
            for (String child : entry.getValue()) {
                RoleRelationship relationship = new RoleRelationship();
                relationship.setRoleId(roleId);
                relationship.setChildId(child);
                roleRelationships.add(relationship);
            }
        }
        return roleRelationships;
    }

    private List<PermissionRelationship> analysePermissionRelations(List<Permission> permissions,
        List<Relation> relations) {
        Map<String, Set<String>> adjacencyMap = transferToAdjacencyMap(relations);
        if (checkCircle(adjacencyMap)) {
            throw new WebException(HttpStatus.BAD_REQUEST, "权限发现环状关系");
        }
        List<PermissionRelationship> permissionRelationships = new ArrayList<>();

        // self
        for (Permission permission : permissions) {
            PermissionRelationship self = new PermissionRelationship();
            self.setAncestorId(permission.getId());
            self.setDescendantId(permission.getId());
            self.setAdjoin(0);
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
        String ancestor, List<PermissionRelationship> permissionRelations,
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
            int adjoin = children.contains(descendant) ? 1 : 2;
            PermissionRelationship relationship = new PermissionRelationship();
            relationship.setAncestorId(ancestor);
            relationship.setDescendantId(descendant);
            relationship.setAdjoin(adjoin);
            permissionRelations.add(relationship);
        }
        cache.put(ancestor, descendants);
        return descendants;
    }


    private List<RolePermission> analyseRolePermissions(List<Relation> roleRelations,
        List<Relation> rolePermissions, List<Permission> permissions) {
        List<RolePermission> rps = new ArrayList<>();
        Map<String, Set<String>> roleAdjMap = transferToAdjacencyMap(roleRelations);
        Map<String, Set<String>> roleAdjPermissionsMap = transferToAdjacencyMap(rolePermissions);
        Set<String> permissionIds = permissions.stream()
            .map(Permission::getId)
            .collect(Collectors.toSet());

        Map<String, Set<String>> cache = new HashMap<>(256);
        for (String roleId : roleAdjMap.keySet()) {
            dfsFindRoleDescendants(roleAdjMap, permissionIds, roleId, rps, roleAdjPermissionsMap,
                cache);
        }
        return rps;
    }

    private Set<String> dfsFindRoleDescendants(Map<String, Set<String>> adjacencyMap,
        Set<String> permissionIds, String subject, List<RolePermission> rps,
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
            if(!permissionIds.contains(permission)) {
                continue;
            }
            int self = selfPermissions.contains(permission) ? 1 : 2;
            RolePermission rp = new RolePermission();
            rp.setRoleId(subject);
            rp.setPermissionId(permission);
            rp.setSelf(self);
            rps.add(rp);
        }

        cache.put(subject, descendants);
        return descendants;
    }
}
