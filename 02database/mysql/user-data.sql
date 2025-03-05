insert ignore into t_user_account (id, username, encrypt_password, salt, login_time,
                                   register_key, account_status)
values (0, 'admin', '5ff6689115c8eb335d0f06a52d2fcbfca19a74296626e3fd607f623de606d886', 'abcdefgh',
        null, '', 1);

insert ignore into t_user_info(user_id, nick_name, avatar, description, home)
values (0, 'administrator', '/avatar/avatar-10.png', 'super administrator', '/home');

insert ignore into t_user_roles(user_id, role_id)
values (0, 'role.admin');

insert ignore into t_role(role_id, name, description)
values ('role.superadmin', '超级管理员', '超级管理员'),
       ('role.admin', '管理员', '管理员');

insert ignore into t_role_permissions(role_id, permission_id, self)
values ('role.admin', 'permission.admin', 1);

insert ignore into t_permission(permission_id, name, description)
values ('permission.admin', '管理员权限', '管理员权限');

insert ignore into t_permission_relationships(ancestor_id, descendant_id, adjoin)
values ('permission.admin', 'permission.admin', 0)