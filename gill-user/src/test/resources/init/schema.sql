create table if not exists `t_user`
(
    `id`               int primary key comment '用户id',
    `username`         varchar(16) not null comment '登录账号',
    `encrypt_password` varchar(64) not null comment '加密密码',
    `salt`             varchar(32) not null comment '盐',
    `create_time`      datetime comment '创建时间',
    `login_time`       datetime comment '最后登录时间',
    `nick_name`        varchar(16) not null comment '用户昵称',
    `avatar`           varchar(64) comment '头像图标',
    `description`      varchar(64) comment '个人描述'
);

create unique index idx_username on t_user (`username`);
create index idx_nick on t_user (`nick_name`);

create table if not exists `t_user_ban`
(
    `user_id`    int comment '用户id',
    `until_time` datetime comment '封禁的结束时间',
    `reason`     varchar(128) default '' comment '封禁的原因'
);
create index idx_user on t_user_ban (`user_id`, `until_time` desc);

create table if not exists `t_user_friends`
(
    `user_id`     int                                  not null comment '用户id',
    `friend_id`   int                                  not null comment '好友id',
    `status`      int      default 0                   not null comment '好友状态: 0=未确认，1=已确认',
    `create_time` datetime default current_timestamp() not null comment '成为好友的时间',
    primary key (`user_id`, `friend_id`)
);
create index idx_user_status on t_user_friends (`user_id`, `status`);
create index idx_friend_status on t_user_friends (`friend_id`, `status`);

create table if not exists `t_role`
(
    `id`          varchar(32) primary key comment '通用角色ID',
    `name`        varchar(16)  not null comment '角色名称',
    `description` varchar(128) not null default '' comment '角色描述'
);

create table if not exists `t_role_relationships`
(
    `role_id`  varchar(32) comment '通用角色ID',
    `child_id` varchar(32) comment '孩子角色ID',
    primary key (`role_id`, `child_id`)
);

create table if not exists `t_role_permissions`
(
    `role_id`       varchar(32) not null comment '角色id',
    `permission_id` varchar(32) not null comment '权限id',
    `self`          int default 0 comment '1: 自己的权限, 2: 孩子节点的权限',
    primary key (`role_id`, `permission_id`)
);
create index idx_role_self on t_role_permissions (`role_id`, `self`);

create table if not exists `t_permission`
(
    `id`          varchar(32) primary key comment '权限id',
    `name`        varchar(16)  not null comment '权限名称',
    `description` varchar(128) not null default '' comment '权限描述'
);

create table if not exists `t_permission_relationships`
(
    `ancestor_id`   varchar(32) not null comment '祖先节点id',
    `descendant_id` varchar(32) not null comment '后代节点id',
    `adjoin`        int default 0 comment '0: 自己, 1: 是直接后代, 2: 非直接后代',
    primary key (`ancestor_id`, `descendant_id`)
);
create index idx_descendant on t_permission_relationships (`descendant_id`, `ancestor_id`);
create index idx_ancestor_adjoin on t_permission_relationships (`ancestor_id`, `adjoin`);

create table if not exists `t_user_roles`
(
    `user_id` int         not null comment '用户id',
    `role_id` varchar(32) not null comment '角色id',
    primary key (`user_id`, `role_id`)
);
