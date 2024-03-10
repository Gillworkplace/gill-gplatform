drop database if exists chat;
create database if not exists chat;
use chat;

create table if not exists `t_voice_room`
(
    `id`          int primary key comment '房间id',
    `name`        varchar(16)  not null comment '房间名称',
    `description` varchar(128) not null comment '房间描述'
)