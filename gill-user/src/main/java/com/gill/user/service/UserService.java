package com.gill.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.expression.ExpressionUtil;
import com.gill.api.constant.AccountStatusEnum;
import com.gill.api.domain.UserProperties;
import com.gill.api.model.User;
import com.gill.api.service.oss.IOssService;
import com.gill.api.service.user.IUserService;
import com.gill.common.crypto.CryptoFactory;
import com.gill.common.crypto.CryptoStrategy;
import com.gill.dubbo.contant.Filters;
import com.gill.redis.core.Redis;
import com.gill.user.config.RoleMap;
import com.gill.user.domain.UserDetail;
import com.gill.user.dto.UserInfo;
import com.gill.user.dto.param.RegisterParam;
import com.gill.user.entity.UserAccountEntity;
import com.gill.user.entity.UserBanEntity;
import com.gill.user.entity.UserInfoEntity;
import com.gill.user.service.mapperservice.UserAccountService;
import com.gill.user.service.mapperservice.UserBanService;
import com.gill.user.service.mapperservice.UserInfoService;
import com.gill.web.exception.WebException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
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
    private UserAccountService userAccountService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserBanService userBanService;

    @DubboReference(filter = Filters.CONSUMER, check = false, lazy = true)
    private IOssService ossService;

    /**
     * 预校验用户名是否已存在
     *
     * @param username 用户名
     */
    public void precheckUsername(String username) {
        boolean exists = userAccountService.lambdaQuery()
            .eq(UserAccountEntity::getUsername, username)
            .eq(UserAccountEntity::getDeleted, false)
            .exists();
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
    public long registerUser(RegisterParam param) {
        return registerUserWithRole(param, RoleMap.NORMAL_USER);
    }

    /**
     * 注册用户账号
     *
     * @param param 参数
     * @return userId
     */
    @Transactional(rollbackFor = Exception.class)
    public long registerUserWithRole(RegisterParam param, Set<String> roles) {

        // 获取用户ID
        long userId = IdUtil.getSnowflakeNextId();

        try {
            UserAccountEntity userAccount = generateUserAccount(param, userId);
            userAccountService.save(userAccount);

            UserInfoEntity userInfo = generateUserInfo(param, userId);
            userInfoService.save(userInfo);
        } catch (DuplicateKeyException e) {
            throw new WebException(HttpStatus.BAD_REQUEST, "账号已存在");
        }

        // 设置用户角色
        resourceService.addUserRoles(userId, roles);
        return userId;
    }

    private UserAccountEntity generateUserAccount(RegisterParam param, long userId) {
        UserAccountEntity userAccount = new UserAccountEntity();
        userAccount.setId(userId);
        userAccount.setUsername(param.getUsername());
        userAccount.setSalt(generateSalt());
        userAccount.setEncryptPassword(digestPwd(param.getPassword(), userAccount.getSalt()));
        userAccount.setRegisterKey(param.getInviteKey());
        userAccount.setAccountStatus(AccountStatusEnum.UNUSED.getCode());
        return userAccount;
    }

    private UserInfoEntity generateUserInfo(RegisterParam param, Long userId) {
        UserInfoEntity user = new UserInfoEntity();
        user.setUserId(userId);
        user.setNickName(param.getNickName());
        user.setAvatar(randomDefaultAvatar());
        user.setDescription(param.getDescription());
        user.setHome(UserProperties.DEFAULT_HOME);
        return user;
    }

    private String randomDefaultAvatar() {
        List<String> defaultAvatarList = ossService.getDefaultAvatarList();
        int randomIdx = RandomUtil.randomInt(defaultAvatarList.size());
        return defaultAvatarList.get(randomIdx);
    }

    /**
     * 检查用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return userid
     */
    public long checkLogin(@NonNull String username, @NonNull String password) {
        UserAccountEntity userAccount = userAccountService.lambdaQuery()
            .select(UserAccountEntity::getId, UserAccountEntity::getUsername,
                UserAccountEntity::getEncryptPassword, UserAccountEntity::getSalt)
            .eq(UserAccountEntity::getUsername, username)
            .eq(UserAccountEntity::getDeleted, false)
            .one();

        if (userAccount == null) {
            throw new WebException(HttpStatus.BAD_REQUEST, "用户名不存在或密码错误");
        }

        String salt = userAccount.getSalt();
        String encryptPassword = userAccount.getEncryptPassword();
        if (!encryptPassword.equals(digestPwd(password, salt))) {
            throw new WebException(HttpStatus.BAD_REQUEST, "用户名不存在或密码错误");
        }
        return userAccount.getId();
    }

    /**
     * 检查用户当前是否被封禁
     *
     * @param userId 用户ID
     */
    public void checkAccess(long userId) {
        UserBanEntity entity = userBanService.lambdaQuery()
            .eq(UserBanEntity::getUserId, userId)
            .gt(UserBanEntity::getUntilTime, LocalDateTime.now())
            .eq(UserBanEntity::getDeleted, false)
            .orderByAsc(UserBanEntity::getUntilTime)
            .last("limit 1")
            .one();
        if (entity != null) {
            throw new WebException(HttpStatus.FORBIDDEN, entity.getReason());
        }
    }

    /**
     * 成功登录
     *
     * @param userId 用户ID
     * @return token
     */
    public UserDetail successLoginAndGenerateToken(long userId) {

        // 更新登录时间
        userAccountService.lambdaUpdate()
            .set(UserAccountEntity::getLoginTime, LocalDateTime.now())
            .eq(UserAccountEntity::getId, userId)
            .eq(UserAccountEntity::getDeleted, false)
            .update();

        // 生成token
        UserAccountEntity userAccount = userAccountService.lambdaQuery()
            .select(UserAccountEntity::getUsername)
            .eq(UserAccountEntity::getId, userId)
            .eq(UserAccountEntity::getDeleted, false)
            .one();
        if (userAccount == null) {
            throw new WebException(HttpStatus.BAD_REQUEST, "用户不存在");
        }

        String username = userAccount.getUsername();

        UserInfoEntity userInfo = userInfoService.lambdaQuery()
            .select(UserInfoEntity::getUserId, UserInfoEntity::getNickName,
                UserInfoEntity::getAvatar, UserInfoEntity::getDescription, UserInfoEntity::getHome)
            .eq(UserInfoEntity::getUserId, userId)
            .eq(UserInfoEntity::getDeleted, false)
            .one();
        String token = generateToken();

        User user = new User();
        user.setId(userInfo.getUserId());
        user.setUsername(username);
        user.setNickName(userInfo.getNickName());
        user.setAvatar(userInfo.getAvatar());
        user.setDescription(userInfo.getDescription());
        user.setHome(userInfo.getHome());
        user.setLoginTime(userAccount.getLoginTime());
        user.setCreateTime(userAccount.getCreateTime());

        redis.mset(UserProperties.getRedisTokenKey(token), generateRedisUserInfo(username, user));

        // 存储用户权限缓存
        Set<String> permissions = resourceService.refreshUserPermissions(userId);
        return new UserDetail(token, user, permissions);
    }

    private Map<String, Object> generateRedisUserInfo(String username, User user) {
        Map<String, Object> userInfoMap = new HashMap<>(16);
        userInfoMap.put(UserProperties.USER_ID, String.valueOf(user.getId()));
        userInfoMap.put(UserProperties.USER_NAME, user.getUsername());
        userInfoMap.put(UserProperties.NICK_NAME, user.getNickName());
        userInfoMap.put(UserProperties.AVATAR, user.getAvatar());
        userInfoMap.put(UserProperties.DESCRIPTION, user.getDescription());
        userInfoMap.put(UserProperties.HOME, UserProperties.DEFAULT_HOME);
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
    public UserInfo getUserInfo(long userId, String token) {
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
    public void checkToken(Long userId, String token) {
        Long uid = redis.mget(UserProperties.getRedisTokenKey(token), UserProperties.USER_ID,
            Long.class);
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
    public void checkPermission(Long uid, String permissionExpression, int exceptionCode,
        String exceptionMessage) {
        Set<String> permissions = redis.sget(UserProperties.getRedisUserResourceKey(uid));
        if (!doCheckPermission(permissions, permissionExpression)) {
            HttpStatus status = HttpStatus.resolve(exceptionCode);
            throw new WebException(Objects.requireNonNullElse(status, HttpStatus.FORBIDDEN),
                exceptionMessage);
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

    /**
     * 退出登录
     *
     * @param userId 用户ID
     * @param token  token
     */
    public void logout(long userId, String token) {

        // 清除redis token 信息
        redis.clear(UserProperties.getRedisTokenKey(token));
    }
}
