package com.gill.user.mappers;

import com.gill.api.model.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserMapper
 *
 * @author gill
 * @version 2024/02/06
 **/
@Mapper
public interface UserMapper {

    /**
     * 根据用户名获取加密密码和盐
     *
     * @param username 用户名
     * @return uid, 密码, 盐
     */
    User getEncryptPwdAndSaltByUsername(String username);

    /**
     * 更新登录时间
     *
     * @param userId 用户ID
     */
    void updateLoginTime(int userId);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息不包含敏感信息（如密码等）
     */
    User getUserInfoById(int userId);

    /**
     * 插入用户信息
     *
     * @param user 用户信息
     */
    void insertUser(User user);

    /**
     * 用户名是否已存在
     *
     * @param username 用户名
     */
    boolean existsUsername(String username);
}
