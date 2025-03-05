package com.gill.user.mapper;

import com.gill.user.entity.UserInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author zhangzhiyan
 * @since 2025-03-05
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfoEntity> {

}
