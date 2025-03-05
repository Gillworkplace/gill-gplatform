package com.gill.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户封禁表
 * </p>
 *
 * @author zhangzhiyan
 * @since 2025-03-05
 */
@Getter
@Setter
@TableName("t_user_ban")
public class UserBanEntity {

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 封禁的结束时间
     */
    @TableField("until_time")
    private LocalDateTime untilTime;

    /**
     * 封禁的原因
     */
    @TableField("reason")
    private String reason;

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
