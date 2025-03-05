package com.gill.user;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.gill.common.util.ObjectUtil;
import com.gill.user.domain.Permissions;
import com.gill.user.domain.Relation;
import com.gill.user.domain.Roles;
import com.gill.user.entity.PermissionEntity;
import com.gill.user.entity.PermissionRelationshipsEntity;
import com.gill.user.entity.RoleEntity;
import com.gill.user.entity.RolePermissionsEntity;
import com.gill.user.entity.RoleRelationshipsEntity;
import com.gill.user.service.ResourceService;
import com.gill.user.service.mapperservice.PermissionRelationshipsService;
import com.gill.user.service.mapperservice.PermissionService;
import com.gill.user.service.mapperservice.RolePermissionsService;
import com.gill.user.service.mapperservice.RoleRelationshipsService;
import com.gill.user.service.mapperservice.RoleService;
import com.gill.web.exception.WebException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;

/**
 * ResourceTests 10以后为角色权限入库后的场景
 *
 * @author gill
 * @version 2024/02/08
 **/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.config.location=classpath:application-resource.yaml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ResourceTests extends AbstractTest {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleRelationshipsService roleRelationshipsService;

    @Autowired
    private PermissionRelationshipsService permissionRelationshipsService;

    @Autowired
    private RolePermissionsService rolePermissionsService;

    @Test
    public void test_read_roles_yaml() {

        Roles roles = ObjectUtil.readYamlFromClasspath("roles-test.yaml", Roles.class);
        RoleEntity admin = new RoleEntity();
        admin.setId("role.admin");
        admin.setName("管理员");
        admin.setDescription("超级管理员");

        RoleEntity normal = new RoleEntity();
        normal.setId("role.normal");
        normal.setName("普通用户");
        normal.setDescription("正常注册的用户");

        Relation roleRelation = new Relation();
        roleRelation.setSubject("role.admin");
        roleRelation.setItems(List.of("role.normal"));

        Relation roleResources1 = new Relation();
        roleResources1.setSubject("role.admin");
        roleResources1.setItems(List.of("permission.admin"));

        Relation roleResources2 = new Relation();
        roleResources2.setSubject("role.normal");
        roleResources2.setItems(List.of("permission.home"));

        Roles expect = new Roles();
        expect.setRoles(List.of(admin, normal));
        expect.setRoleRelations(List.of(roleRelation));
        expect.setRolePermissions(List.of(roleResources1, roleResources2));
        Assertions.assertEquals(JSONUtil.toJsonStr(expect), JSONUtil.toJsonStr(roles));
    }

    @Test
    public void test_read_permissions_yaml() {
        Permissions permissions = ObjectUtil.readYamlFromClasspath("permission-test.yaml",
            Permissions.class);
        PermissionEntity admin = new PermissionEntity();
        admin.setId("permission.admin");
        admin.setName("管理员资源");
        admin.setDescription("管理员总资源");

        PermissionEntity adminRead = new PermissionEntity();
        adminRead.setId("permission.admin.read");
        adminRead.setName("管理员资源-读");
        adminRead.setDescription("管理员资源-读");

        PermissionEntity home = new PermissionEntity();
        home.setId("permission.home");
        home.setName("首页");
        home.setDescription("首页资源");

        Relation relation = new Relation();
        relation.setSubject("permission.admin");
        relation.setItems(List.of("permission.admin.read"));

        Permissions expect = new Permissions();
        expect.setPermissions(List.of(admin, adminRead, home));
        expect.setRelations(List.of(relation));
        Assertions.assertEquals(JSONUtil.toJsonStr(expect), JSONUtil.toJsonStr(permissions));
    }

    private <T> List<T> readExpectData(String file, Class<T> clazz) throws Exception {
        String json = FileUtil.readString(new ClassPathResource(file).getFile(),
            StandardCharsets.UTF_8);
        return JSONUtil.toList(json, clazz);
    }

    @Order(1)
    @Transactional
    @Test
    public void test_analyse_roles_yaml_find_circle_should_throw_web_exception() {
        Roles roles = ObjectUtil.readYamlFromClasspath("roles-circle.yaml", Roles.class);
        Permissions permissions = ObjectUtil.readYamlFromClasspath("permission.yaml",
            Permissions.class);
        WebException webException = Assertions.assertThrows(WebException.class,
            () -> resourceService.parseAndInsertIntoDb(roles, permissions));
        Assertions.assertEquals("角色发现环状关系", webException.getMessage());
    }

    @Order(1)
    @Transactional
    @Test
    public void test_analyse_permission_yaml_find_circle_should_throw_web_exception() {
        Roles roles = ObjectUtil.readYamlFromClasspath("roles.yaml", Roles.class);
        Permissions permissions = ObjectUtil.readYamlFromClasspath("permission-circle.yaml",
            Permissions.class);
        WebException webException = Assertions.assertThrows(WebException.class,
            () -> resourceService.parseAndInsertIntoDb(roles, permissions));
        Assertions.assertEquals("权限发现环状关系", webException.getMessage());
    }

    @Order(1)
    @Transactional
    @Test
    public void test_analyse_complex_relations_should_be_expected() throws Exception {
        Roles roles = ObjectUtil.readYamlFromClasspath("roles-complex.yaml", Roles.class);
        Permissions permissions = ObjectUtil.readYamlFromClasspath("permission-complex.yaml",
            Permissions.class);
        resourceService.parseAndInsertIntoDb(roles, permissions);

        // actual
        List<RoleEntity> r = roleService.list();
        List<PermissionEntity> p = permissionService.list();
        List<RoleRelationshipsEntity> rr = roleRelationshipsService.list();
        List<PermissionRelationshipsEntity> pr = permissionRelationshipsService.list();
        List<RolePermissionsEntity> rp = rolePermissionsService.list();

        // expected
        List<RoleEntity> er = readExpectData("expect-roles-complex.json", RoleEntity.class);
        List<PermissionEntity> ep = readExpectData("expect-permissions-complex.json",
            PermissionEntity.class);
        List<RoleRelationshipsEntity> err = readExpectData("expect-role-relations-complex.json",
            RoleRelationshipsEntity.class);
        List<PermissionRelationshipsEntity> epr = readExpectData(
            "expect-permission-relations-complex.json", PermissionRelationshipsEntity.class);
        List<RolePermissionsEntity> erp = readExpectData("expect-role-permission-complex.json",
            RolePermissionsEntity.class);

        r.forEach(entity -> {
            entity.setCreateTime(null);
            entity.setUpdateTime(null);
            entity.setDeleted(null);
        });
        Assertions.assertEquals(JSONUtil.toJsonStr(er), JSONUtil.toJsonStr(r));

        p.forEach(entity -> {
            entity.setCreateTime(null);
            entity.setUpdateTime(null);
            entity.setDeleted(null);
        });
        Assertions.assertEquals(JSONUtil.toJsonStr(ep), JSONUtil.toJsonStr(p));

        rr.forEach(entity -> {
            entity.setCreateTime(null);
            entity.setUpdateTime(null);
            entity.setDeleted(null);
        });
        Assertions.assertEquals(JSONUtil.toJsonStr(err), JSONUtil.toJsonStr(rr));

        pr.forEach(entity -> {
            entity.setCreateTime(null);
            entity.setUpdateTime(null);
            entity.setDeleted(null);
        });
        Assertions.assertEquals(JSONUtil.toJsonStr(epr), JSONUtil.toJsonStr(pr));

        rp.forEach(entity -> {
            entity.setCreateTime(null);
            entity.setUpdateTime(null);
            entity.setDeleted(null);
        });
        Assertions.assertEquals(JSONUtil.toJsonStr(erp), JSONUtil.toJsonStr(rp));
    }

    @Order(10)
    @Test
    public void test_analyse_normal_relations_should_be_success() {
        Roles roles = ObjectUtil.readYamlFromClasspath("roles-test.yaml", Roles.class);
        Permissions permissions = ObjectUtil.readYamlFromClasspath("permission-test.yaml",
            Permissions.class);
        resourceService.parseAndInsertIntoDb(roles, permissions);
    }

    @Test
    @Order(11)
    public void test_contains_role_should_return_true() {
        Assertions.assertTrue(resourceService.containsRole("role.admin"));
    }

    @Test
    @Order(11)
    public void test_contains_role_should_return_false() {
        Assertions.assertFalse(resourceService.containsRole("role.unknown"));
    }
}
