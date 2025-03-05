insert into t_user_account (id, username, encrypt_password, salt, login_time,
                                   register_key, account_status)
values (0, 'admin', '5ff6689115c8eb335d0f06a52d2fcbfca19a74296626e3fd607f623de606d886', 'abcdefgh',
        null, '', 1),
       (1, 'test', '5ff6689115c8eb335d0f06a52d2fcbfca19a74296626e3fd607f623de606d886', 'abcdefgh',
        null, '', 1);

insert into t_user_info(user_id, nick_name, avatar, description, home)
values (0, '管理员', 'https://cdn.jsdelivr.net/gh/IT-JUNKIES/CDN-FILES/img/avatar.png',
        '我是管理员', '/admin'),
       (1, '测试用户', 'https://cdn.jsdelivr.net/gh/IT-JUNKIES/CDN-FILES/img/avatar.png',
        '我是测试用户', '/home');

insert into t_user_roles(user_id, role_id)
values (0, 'role.admin'),
       (1, 'role.normal');