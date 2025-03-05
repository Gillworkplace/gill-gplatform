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
 * 权限关系表
 * </p>
 *
 * @author zhangzhiyan
 * @since 2025-03-05
 */
@Getter
@Setter
@TableName("t_permission_relationships")
public class PermissionRelationshipsEntity {

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 祖先节点id
     */
    @TableField("ancestor_id")
    private String ancestorId;

    /**
     * 后代节点id
     */
    @TableField("descendant_id")
    private String descendantId;

    /**
     * 0: 自己, 1: 是直接后代, 2: 非直接后代
     */
    @TableField("adjoin")
    private Integer adjoin;

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
