-- 账号表
create table if not exists `t_user_account`
(
    `id`               bigint primary key comment '用户id',
    `username`         varchar(16) not null comment '登录账号',
    `encrypt_password` varchar(64) not null comment '加密密码',
    `salt`             varchar(32) not null comment '盐',
    `login_time`       datetime    null comment '最后登录时间',
    `register_key`     varchar(16) not null default '' comment '注册使用的key',
    `account_status`   tinyint     not null default 0 comment '账号状态 0-未使用 1-正常使用 2-被限制',
    `create_time`      datetime    not null default CURRENT_TIMESTAMP comment '创建时间',
    `update_time`      datetime    not null default CURRENT_TIMESTAMP comment '更新时间',
    `deleted`          boolean     not null default 0 comment '逻辑删除'
);

-- 用户信息表
create table if not exists `t_user_info`
(
    `user_id`     bigint primary key comment '用户ID',
    `nick_name`   varchar(16) not null comment '用户昵称',
    `avatar`      varchar(64) comment '头像图标',
    `description` varchar(64) comment '个人描述',
    `home`        varchar(32) comment '登录首页',
    `create_time` datetime    not null default CURRENT_TIMESTAMP comment '创建时间',
    `update_time` datetime    not null default CURRENT_TIMESTAMP comment '更新时间',
    `deleted`     boolean     not null default 0 comment '逻辑删除'
);

-- 邀请码表
create table if not exists `t_user_invite_key`
(
    `id`          int auto_increment primary key comment '自增ID',
    `user_id`     bigint     not null comment '用户ID',
    `invite_key`  varchar(8) not null comment '邀请码',
    `create_time` datetime   not null default CURRENT_TIMESTAMP comment '创建时间',
    `update_time` datetime   not null default CURRENT_TIMESTAMP comment '更新时间',
    `delete_time` datetime   null comment '删除时间',
    `deleted`     boolean    not null default 0 comment '逻辑删除'
);

-- 用户封禁表
create table if not exists `t_user_ban`
(
    `user_id`     bigint comment '用户id',
    `until_time`  datetime comment '封禁的结束时间',
    `reason`      varchar(128)      default '' comment '封禁的原因',
    `create_time` datetime not null default CURRENT_TIMESTAMP comment '创建时间',
    `update_time` datetime not null default CURRENT_TIMESTAMP comment '更新时间',
    `deleted`     boolean  not null default 0 comment '逻辑删除'
);

-- 用户好友表
create table if not exists `t_user_friends`
(
    `id`            int auto_increment primary key comment '自增ID',
    `user_id`       bigint   not null comment '用户id',
    `friend_id`     int      not null comment '好友id',
    `friend_status` int               default 0 not null comment '好友状态: 0=未确认，1=已确认',
    `create_time`   datetime not null default CURRENT_TIMESTAMP comment '创建时间',
    `update_time`   datetime not null default CURRENT_TIMESTAMP comment '更新时间',
    `deleted`       boolean  not null default 0 comment '逻辑删除',
    `delete_time`   datetime null comment '删除时间'
);

create table if not exists `t_role`
(
    `id`          varchar(32) primary key comment '通用角色ID',
    `name`        varchar(16)  not null comment '角色名称',
    `description` varchar(128) not null default '' comment '角色描述',
    `create_time` datetime     not null default CURRENT_TIMESTAMP comment '创建时间',
    `update_time` datetime     not null default CURRENT_TIMESTAMP comment '更新时间',
    `deleted`     boolean      not null default 0 comment '逻辑删除'
);

create table if not exists `t_role_relationships`
(
    `id`          int auto_increment primary key comment '自增ID',
    `role_id`     varchar(32) comment '通用角色ID',
    `child_id`    varchar(32) comment '孩子角色ID',
    `create_time` datetime not null default CURRENT_TIMESTAMP comment '创建时间',
    `update_time` datetime not null default CURRENT_TIMESTAMP comment '更新时间',
    `deleted`     boolean  not null default 0 comment '逻辑删除'
);

create table if not exists `t_role_permissions`
(
    `id`            int auto_increment primary key comment '自增ID',
    `role_id`       varchar(32) not null comment '角色id',
    `permission_id` varchar(32) not null comment '权限id',
    `self`          int                  default 0 comment '1: 自己的权限, 2: 孩子节点的权限',
    `create_time`   datetime    not null default CURRENT_TIMESTAMP comment '创建时间',
    `update_time`   datetime    not null default CURRENT_TIMESTAMP comment '更新时间',
    `deleted`       boolean     not null default 0 comment '逻辑删除'
);

create table if not exists `t_permission`
(
    `id`          varchar(32) primary key comment '权限id',
    `name`        varchar(16)  not null comment '权限名称',
    `description` varchar(128) not null default '' comment '权限描述',
    `create_time` datetime     not null default CURRENT_TIMESTAMP comment '创建时间',
    `update_time` datetime     not null default CURRENT_TIMESTAMP comment '更新时间',
    `deleted`     boolean      not null default 0 comment '逻辑删除'
);

create table if not exists `t_permission_relationships`
(
    `id`            int auto_increment primary key comment '自增ID',
    `ancestor_id`   varchar(32) not null comment '祖先节点id',
    `descendant_id` varchar(32) not null comment '后代节点id',
    `adjoin`        int         not null default 0 comment '0: 自己, 1: 是直接后代, 2: 非直接后代',
    `create_time`   datetime    not null default CURRENT_TIMESTAMP comment '创建时间',
    `update_time`   datetime    not null default CURRENT_TIMESTAMP comment '更新时间',
    `deleted`       boolean     not null default 0 comment '逻辑删除'
);

create table if not exists `t_user_roles`
(
    `id`          int auto_increment primary key comment '自增ID',
    `user_id`     bigint      not null comment '用户id',
    `role_id`     varchar(32) not null comment '角色id',
    `create_time` datetime    not null default CURRENT_TIMESTAMP comment '创建时间',
    `update_time` datetime    not null default CURRENT_TIMESTAMP comment '更新时间',
    `deleted`     boolean     not null default 0 comment '逻辑删除',
    `delete_time` datetime    null comment '删除时间'
);