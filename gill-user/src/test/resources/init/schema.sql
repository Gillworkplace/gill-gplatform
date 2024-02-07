create table if not exists `t_user`
(
    `id`               int primary key comment '用户id',
    `username`         varchar(16)  not null comment '登录账号',
    `encrypt_password` varchar(64) not null comment '加密密码',
    `salt`             varchar(32)  not null comment '盐',
    `create_time`      datetime comment '创建时间',
    `login_time`       datetime comment '最后登录时间',
    `nick_name`        varchar(16)  not null comment '用户昵称',
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

create table if not exists `t_resources`
(
    `id`          int primary key comment '资源id',
    `name`        varchar(16)  not null comment '资源名称',
    `description` varchar(128) not null default '' comment '资源描述'
);

create table if not exists `t_resources_relationships`
(
    `ancestor_id`   int not null comment '祖先节点id',
    `descendant_id` int not null comment '后代节点id',
    `distance`      int not null comment '后代节点与祖先节点的距离',
    primary key (`ancestor_id`, `descendant_id`)
);