package com.gill.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户账号表
 * </p>
 *
 * @author zhangzhiyan
 * @since 2025-03-05
 */
@Getter
@Setter
@TableName("t_user_account")
public class UserAccountEntity {

    /**
     * 用户id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 登录账号
     */
    @TableField("username")
    private String username;

    /**
     * 加密密码
     */
    @TableField("encrypt_password")
    private String encryptPassword;

    /**
     * 盐
     */
    @TableField("salt")
    private String salt;

    /**
     * 最后登录时间
     */
    @TableField("login_time")
    private LocalDateTime loginTime;

    /**
     * 注册使用的key
     */
    @TableField("register_key")
    private String registerKey;

    /**
     * 账号状态 0-未使用 1-正常使用 2-被限制
     */
    @TableField("account_status")
    private Byte accountStatus;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableField("deleted")
    private Boolean deleted;
}
