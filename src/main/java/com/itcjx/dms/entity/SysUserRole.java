package com.itcjx.dms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 用户-角色关联表
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
@Getter
@Setter
@ToString
@TableName("sys_user_role")
public class SysUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联sys_user.id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 关联sys_role.id
     */
    @TableField("role_id")
    private Long roleId;
}
