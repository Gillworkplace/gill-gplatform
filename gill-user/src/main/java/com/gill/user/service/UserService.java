package com.gill.user.service;

import cn.hutool.core.lang.UUID;
import com.gill.api.domain.UserProperties;
import com.gill.api.model.User;
import com.gill.api.model.UserBan;
import com.gill.common.crypto.CryptoFactory;
import com.gill.common.crypto.CryptoStrategy;
import com.gill.redis.core.Redis;
import com.gill.user.dto.RegisterParam;
import com.gill.user.mapper.UserBanMapper;
import com.gill.user.mapper.UserMapper;
import com.gill.web.exception.WebException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * LoginService
 *
 * @author gill
 * @version 2024/02/06
 **/
@Component
@Slf4j
public class UserService {

    @Autowired
    private Redis redis;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserBanMapper userBanMapper;

    /**
     * 预校验用户名是否已存在
     *
     * @param username 用户名
     */
    public void precheckUsername(String username) {
        boolean exists = userMapper.existsUsername(username);
        if (exists) {
            throw new WebException(HttpStatus.BAD_REQUEST, "账号已存在");
        }
    }

    /**
     * 注册用户账号
     *
     * @param param 参数
     * @return userId
     */
    public int registerUser(RegisterParam param) {

        // 获取用户ID
        Long userId = redis.increaseAndGet(UserProperties.REDIS_USER_ID_KEY);
        if (userId == null) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "服务器异常");
        }

        // 插入用户数据
        User user = generateUser(param, userId);
        try {
            userMapper.insertUser(user);
        } catch (DuplicateKeyException e) {
            throw new WebException(HttpStatus.BAD_REQUEST, "账号已存在");
        }

        // TODO extra operation

        return userId.intValue();
    }

    private User generateUser(RegisterParam param, Long userId) {
        User user = new User();
        user.setId(userId.intValue());
        user.setUsername(param.getUsername());
        user.setSalt(generateSalt());
        user.setEncryptPassword(digestPwd(param.getPassword(), user.getSalt()));
        user.setNickName(param.getNickName());
        user.setAvatar(param.getAvatar());
        user.setDescription(param.getDescription());
        return user;
    }

    /**
     * 检查用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return userid
     */
    public int checkLogin(@NonNull String username, @NonNull String password) {
        User user = userMapper.getEncryptPwdAndSaltByUsername(username);
        if (user == null) {
            throw new WebException(HttpStatus.BAD_REQUEST, "用户名不存在或密码错误");
        }
        String salt = user.getSalt();
        String encryptPassword = user.getEncryptPassword();
        if (!encryptPassword.equals(digestPwd(password, salt))) {
            throw new WebException(HttpStatus.BAD_REQUEST, "用户名不存在或密码错误");
        }
        return user.getId();
    }

    /**
     * 检查用户当前是否被封禁
     *
     * @param userId 用户ID
     */
    public void checkAccess(int userId) {
        UserBan userBan = userBanMapper.firstUserBan(userId);
        if (userBan != null) {
            throw new WebException(HttpStatus.FORBIDDEN, userBan.getReason());
        }
    }

    /**
     * 成功登录
     *
     * @param userId 用户ID
     * @return token
     */
    public String successLoginAndGenerateToken(int userId) {
        userMapper.updateLoginTime(userId);
        User user = userMapper.getUserInfoById(userId);
        String token = generateToken();
        redis.mset(UserProperties.getRedisTokenKey(token), user.toRedisUserInfoMap());
        return token;
    }

    private String generateToken() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString(true);
    }

    private String generateSalt() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString(true);
    }

    /**
     * 密码摘要
     *
     * @param password 密码
     * @param salt     盐
     * @return 摘要
     */
    private String digestPwd(String password, String salt) {
        CryptoStrategy sha256 = CryptoFactory.getStrategy("sha256");
        return sha256.encrypt(password + salt);
    }
}
