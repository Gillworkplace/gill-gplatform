package com.gill.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.extra.expression.ExpressionUtil;
import com.gill.api.domain.UserProperties;
import com.gill.api.model.User;
import com.gill.api.model.UserBan;
import com.gill.api.service.user.IUserService;
import com.gill.common.crypto.CryptoFactory;
import com.gill.common.crypto.CryptoStrategy;
import com.gill.redis.core.Redis;
import com.gill.user.config.RoleMap;
import com.gill.user.domain.UserDetail;
import com.gill.user.dto.RegisterParam;
import com.gill.user.dto.UserInfo;
import com.gill.user.mappers.UserBanMapper;
import com.gill.user.mappers.UserMapper;
import com.gill.web.exception.WebException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * LoginService
 *
 * @author gill
 * @version 2024/02/06
 **/
@DubboService
@Component
@Slf4j
public class UserService implements IUserService {

    @Autowired
    private Redis redis;

    @Autowired
    private ResourceService resourceService;

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
    @Transactional(rollbackFor = Exception.class)
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

        // 设置用户角色
        resourceService.addUserRoles(user.getId(), RoleMap.NORMAL_USER);

        return user.getId();
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
    public UserDetail successLoginAndGenerateToken(int userId) {

        // 更新登录时间
        userMapper.updateLoginTime(userId);

        // 生成token
        User user = userMapper.getUserInfoById(userId);
        String token = generateToken();
        redis.mset(UserProperties.getRedisTokenKey(token), generateRedisUserInfo(user));

        // 存储用户权限缓存
        resourceService.refreshUserPermissions(userId);

        return new UserDetail(token, user);
    }

    private Map<String, Object> generateRedisUserInfo(User user) {
        Map<String, Object> userInfoMap = new HashMap<>(16);
        userInfoMap.put(UserProperties.USER_ID, String.valueOf(user.getId()));
        userInfoMap.put(UserProperties.USER_NAME, user.getUsername());
        userInfoMap.put(UserProperties.NICK_NAME, user.getNickName());
        userInfoMap.put(UserProperties.AVATAR, user.getAvatar());
        userInfoMap.put(UserProperties.DESCRIPTION, user.getDescription());
        return userInfoMap;
    }

    private String generateToken() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString(true);
    }

    private String generateSalt() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString(true);
    }

    private String digestPwd(String password, String salt) {
        CryptoStrategy sha256 = CryptoFactory.getStrategy("sha256");
        return sha256.encrypt(password + salt);
    }

    /**
     * 根据token id 获取用户信息
     *
     * @param userId 用户ID
     * @param token  token
     * @return 用户信息
     */
    public UserInfo getUserInfo(int userId, String token) {
        Map<String, Object> map = redis.mget(UserProperties.getRedisTokenKey(token));
        UserInfo userInfo = BeanUtil.mapToBean(map, UserInfo.class, true,
            CopyOptions.create().ignoreError());
        if (userInfo.getUid() != userId) {
            throw new WebException(HttpStatus.UNAUTHORIZED, "未授权登录");
        }
        return userInfo;
    }

    /**
     * 根据userInfo获取用户信息
     *
     * @param token          token id
     * @param userProperties 用户字段
     * @return 用户数据
     */
    @Override
    public com.gill.api.service.user.UserInfo getUserInfoByToken(String token,
        UserProperties... userProperties) {
        return null;
    }

    /**
     * 检查token
     *
     * @param userId 用户Id
     * @param token  token
     */
    public void checkToken(Integer userId, String token) {
        Integer uid = redis.mget(UserProperties.getRedisTokenKey(token), UserProperties.USER_ID,
            Integer.class);
        if (uid == null || !uid.equals(userId)) {
            throw new WebException(HttpStatus.UNAUTHORIZED, "未授权登录");
        }
    }

    /**
     * 检查用户权限
     *
     * @param uid                  用户ID
     * @param permissionExpression 权限表达式
     * @param exceptionCode        异常码
     * @param exceptionMessage     异常消息
     */
    public void checkPermission(Integer uid, String permissionExpression, int exceptionCode,
        String exceptionMessage) {
        Set<String> permissions = redis.sget(UserProperties.getRedisUserResourceKey(uid));
        if (!doCheckPermission(permissions, permissionExpression)) {
            throw new WebException(HttpStatus.resolve(exceptionCode), exceptionMessage);
        }
    }

    private static boolean doCheckPermission(Set<String> permissions, String permissionExpression) {
        Map<String, Object> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        for (char c : permissionExpression.toCharArray()) {
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '.') {
                sb.append(c);
            } else if (!sb.isEmpty()) {
                String permissionKey = sb.toString();
                sb.delete(0, sb.length());
                map.put(permissionKey, permissions.contains(permissionKey));
            }
        }
        if (!sb.isEmpty()) {
            String permissionKey = sb.toString();
            map.put(permissionKey, permissions.contains(permissionKey));
        }
        return Boolean.parseBoolean(String.valueOf(ExpressionUtil.eval(permissionExpression, map)));
    }
}
