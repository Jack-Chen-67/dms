package com.itcjx.dms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itcjx.dms.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户-角色关联表 Mapper 接口
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

}
