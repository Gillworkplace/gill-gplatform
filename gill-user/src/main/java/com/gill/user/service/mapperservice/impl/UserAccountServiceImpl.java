package com.gill.user.service.mapperservice.impl;

import com.gill.user.entity.UserAccountEntity;
import com.gill.user.mapper.UserAccountMapper;
import com.gill.user.service.mapperservice.UserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户账号表 服务实现类
 * </p>
 *
 * @author zhangzhiyan
 * @since 2025-03-05
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccountEntity> implements UserAccountService {

}
