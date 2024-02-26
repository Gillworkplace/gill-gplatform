package com.gill.user;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.gill.api.model.Permission;
import com.gill.api.model.PermissionRelationship;
import com.gill.api.model.Role;
import com.gill.api.model.RolePermission;
import com.gill.api.model.RoleRelationship;
import com.gill.common.util.ObjectUtil;
import com.gill.user.domain.Permissions;
import com.gill.user.domain.Relation;
import com.gill.user.domain.Roles;
import com.gill.user.mappers.ResourceTestMapper;
import com.gill.user.service.ResourceService;
import com.gill.web.exception.WebException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

/**
 * ResourceTests
 *
 * @author gill
 * @version 2024/02/08
 **/
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.config.location=classpath:application-resource.yaml")
public class ResourceTests extends AbstractTest {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceTestMapper resourceTestMapper;

    @Test
    public void test_read_roles_yaml() {

        Roles roles = ObjectUtil.readYamlFromClasspath("roles.yaml", Roles.class);
        Role admin = new Role();
        admin.setId("role.admin");
        admin.setName("管理员");
        admin.setDescription("超级管理员");

        Role normal = new Role();
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
        Permissions permissions = ObjectUtil.readYamlFromClasspath("permission.yaml",
            Permissions.class);
        Permission admin = new Permission();
        admin.setId("permission.admin");
        admin.setName("管理员资源");
        admin.setDescription("管理员总资源");

        Permission adminRead = new Permission();
        adminRead.setId("permission.admin.read");
        adminRead.setName("管理员资源-读");
        adminRead.setDescription("管理员资源-读");

        Permission home = new Permission();
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

    @Transactional
    @Test
    public void test_analyse_complex_relations_should_be_expected() throws Exception {
        Roles roles = ObjectUtil.readYamlFromClasspath("roles-complex.yaml", Roles.class);
        Permissions permissions = ObjectUtil.readYamlFromClasspath("permission-complex.yaml", Permissions.class);
        resourceService.parseAndInsertIntoDb(roles, permissions);

        // actual
        List<Roles> r = resourceTestMapper.queryAllRoles();
        List<Permission> p = resourceTestMapper.queryAllPermissions();
        List<RoleRelationship> rr = resourceTestMapper.queryAllRoleRelationships();
        List<PermissionRelationship> pp = resourceTestMapper.queryAllPermissionRelationships();
        List<RolePermission> rp = resourceTestMapper.queryAllRolePermissions();

        // expected
        List<Role> er = readExpectData("expect-roles-complex.json", Role.class);
        List<Permission> ep = readExpectData("expect-permissions-complex.json", Permission.class);
        List<RoleRelationship> err = readExpectData("expect-role-relations-complex.json",
            RoleRelationship.class);
        List<PermissionRelationship> epp = readExpectData(
            "expect-permission-relations-complex.json", PermissionRelationship.class);
        List<RolePermission> erp = readExpectData("expect-role-permission-complex.json",
            RolePermission.class);

        Assertions.assertEquals(JSONUtil.toJsonStr(er), JSONUtil.toJsonStr(r));
        Assertions.assertEquals(JSONUtil.toJsonStr(ep), JSONUtil.toJsonStr(p));
        Assertions.assertEquals(JSONUtil.toJsonStr(err), JSONUtil.toJsonStr(rr));
        Assertions.assertEquals(JSONUtil.toJsonStr(epp), JSONUtil.toJsonStr(pp));
        Assertions.assertEquals(JSONUtil.toJsonStr(erp), JSONUtil.toJsonStr(rp));
    }
}
