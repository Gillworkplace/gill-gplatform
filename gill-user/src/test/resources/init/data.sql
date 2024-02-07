insert into t_user (id, username, encrypt_password, salt, create_time, login_time, nick_name,
                    avatar, description)
values (0, 'test', '5ff6689115c8eb335d0f06a52d2fcbfca19a74296626e3fd607f623de606d886', 'abcdefgh',
        now(), null, '测试用户', 'https://cdn.jsdelivr.net/gh/IT-JUNKIES/CDN-FILES/img/avatar.png',
        '我是测试用户');