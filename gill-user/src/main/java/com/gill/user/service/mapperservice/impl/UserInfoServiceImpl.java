package com.gill.user.service.mapperservice.impl;

import com.gill.user.entity.UserInfoEntity;
import com.gill.user.mapper.UserInfoMapper;
import com.gill.user.service.mapperservice.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author zhangzhiyan
 * @since 2025-03-05
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfoEntity> implements UserInfoService {

}
