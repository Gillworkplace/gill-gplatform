package com.gill.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.expression.ExpressionUtil;
import com.gill.api.constant.AccountStatusEnum;
import com.gill.api.domain.UserProperties;
import com.gill.api.model.User;
import com.gill.api.service.oss.IOssService;
import com.gill.common.api.DLock;
import com.gill.common.crypto.CryptoFactory;
import com.gill.common.crypto.CryptoStrategy;
import com.gill.common.exception.BusinessException;
import com.gill.common.util.DateUtil;
import com.gill.dubbo.contant.Filters;
import com.gill.redis.core.Redis;
import com.gill.user.config.RoleMap;
import com.gill.user.domain.UserDetail;
import com.gill.user.dto.IndividualProfile;
import com.gill.user.dto.UserInfo;
import com.gill.user.dto.param.IndividualProfileParam;
import com.gill.user.dto.param.RegisterParam;
import com.gill.user.entity.UserAccountEntity;
import com.gill.user.entity.UserBanEntity;
import com.gill.user.entity.UserInfoEntity;
import com.gill.user.entity.UserInviteKeyEntity;
import com.gill.user.service.mapperservice.UserAccountService;
import com.gill.user.service.mapperservice.UserBanService;
import com.gill.user.service.mapperservice.UserInfoService;
import com.gill.user.service.mapperservice.UserInviteKeyService;
import com.gill.web.exception.WebException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
@DubboService(interfaceClass = com.gill.api.service.user.IUserService.class)
@Component
@Slf4j
public class UserService implements IUserService, com.gill.api.service.user.IUserService {

    private static final long EXPIRED_TIME = 7L * 24 * 3600 * 1000;

    @Autowired
    private Redis redis;

    @Autowired
    private DLock lock;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserBanService userBanService;

    @Autowired
    private UserInviteKeyService userInviteKeyService;

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

        // 校验邀请码
        checkInviteKey(param.getInviteKey());

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

        redis.set(UserProperties.getRedisTokenKey(token), user.getId(), EXPIRED_TIME);
        redis.hset(UserProperties.getRedisUserInfoKey(userInfo.getUserId()),
            generateRedisUserInfo(username, user), EXPIRED_TIME);

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

    private static String digestPwd(String password, String salt) {
        CryptoStrategy sha256 = CryptoFactory.getStrategy("sha256");
        return sha256.encrypt(password + salt);
    }

    public static void main(String[] args) {
        System.out.println(digestPwd("!@#QWEasdzxc", "abcdefgh"));
    }

    /**
     * 根据token id 获取用户信息
     *
     * @param userId 用户ID
     * @param token  token
     * @return 用户信息
     */
    public UserInfo getUserInfo(long userId, String token) {
        Map<String, Object> map = redis.hget(UserProperties.getRedisUserInfoKey(userId));
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
        Long uid = redis.get(UserProperties.getRedisTokenKey(token), Long.class);
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

    @Override
    public String getInviteKey(long userId) {
        UserInviteKeyEntity entity = userInviteKeyService.lambdaQuery()
            .select(UserInviteKeyEntity::getInviteKey)
            .eq(UserInviteKeyEntity::getUserId, userId)
            .eq(UserInviteKeyEntity::getDeleted, false)
            .orderByDesc(UserInviteKeyEntity::getCreateTime)
            .last("limit 1")
            .one();
        return Optional.ofNullable(entity).map(UserInviteKeyEntity::getInviteKey).orElse("");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void refreshInviteKey(long userId) {
        lock.tryLock("refresh-invite-key:" + userId, () -> {
            List<UserInviteKeyEntity> keys = userInviteKeyService.lambdaQuery()
                .select(UserInviteKeyEntity::getId)
                .eq(UserInviteKeyEntity::getUserId, userId)
                .eq(UserInviteKeyEntity::getDeleted, false)
                .list();

            Set<Integer> ids = keys.stream()
                .map(UserInviteKeyEntity::getId)
                .collect(Collectors.toSet());
            if (CollectionUtil.isNotEmpty(ids)) {
                userInviteKeyService.lambdaUpdate()
                    .set(UserInviteKeyEntity::getDeleteTime, LocalDateTime.now())
                    .set(UserInviteKeyEntity::getDeleted, true)
                    .in(UserInviteKeyEntity::getId, ids)
                    .update();
            }

            String inviteKey = generateInviteKey(userId);
            UserInviteKeyEntity newKey = new UserInviteKeyEntity();
            newKey.setUserId(userId);
            newKey.setInviteKey(inviteKey);
            userInviteKeyService.save(newKey);
        }, () -> {
            throw new WebException(HttpStatus.TOO_MANY_REQUESTS);
        });
    }

    private String generateInviteKey(long userId) {
        long n1 = userId * 7;
        byte[] bs1 = ByteUtil.longToBytes(n1);
        Assert.isTrue(bs1.length == 8);
        byte[] cp = compress(bs1);
        byte[] salts = RandomUtil.randomBytes(2);

        byte[] keyBytes = new byte[4];
        byte[] merge = magicMerge(cp[0], salts[0]);
        keyBytes[0] = merge[0];
        keyBytes[1] = merge[1];
        merge = magicMerge(cp[1], salts[1]);
        keyBytes[2] = merge[0];
        keyBytes[3] = merge[1];
        return HexUtil.encodeHexStr(keyBytes);
    }

    private static byte[] magicMerge(byte b, byte salt) {
        byte r1 = 0;
        byte r2 = 0;
        for (int i = 0; i < 4; i++) {
            r1 |= (byte) ((b >>> i & 1) << 2 * i);
            r1 |= (byte) ((salt >>> i & 1) << 2 * i + 1);
        }
        for (int i = 4; i < 8; i++) {
            r2 |= (byte) ((b >>> i & 1) << 2 * (i - 4));
            r2 |= (byte) ((salt >>> i & 1) << 2 * (i - 4) + 1);
        }
        return new byte[]{r1, r2};
    }

    private static byte[] magicSplit(byte b1, byte b2) {
        byte b = 0;
        byte salt = 0;
        for (int i = 0; i < 8; i++) {
            if (i % 2 == 0) {
                b |= (byte) ((b1 >>> i & 1) << i / 2);
            } else {
                salt |= (byte) ((b1 >>> i & 1) << i / 2);
            }
        }
        for (int i = 0; i < 8; i++) {
            if (i % 2 == 0) {
                b |= (byte) ((b2 >>> i & 1) << i / 2 + 4);
            } else {
                salt |= (byte) ((b2 >>> i & 1) << i / 2 + 4);
            }
        }
        return new byte[]{b, salt};
    }

    private byte[] compress(byte[] bs) {
        byte[] cp = new byte[2];
        cp[0] = (byte) (bs[0] ^ bs[1] ^ bs[2] ^ bs[3]);
        cp[1] = (byte) (bs[4] ^ bs[5] ^ bs[6] ^ bs[7]);
        return cp;
    }

    /**
     * cp '1011 0011'
     * <p/>
     * salt '1100 1011'
     * <p/>
     * result '1110 0101 1000 1111'
     *
     * @param inviteKey 邀请码
     */
    private void checkInviteKey(String inviteKey) {
        UserInviteKeyEntity entity = userInviteKeyService.lambdaQuery()
            .select(UserInviteKeyEntity::getUserId)
            .eq(UserInviteKeyEntity::getInviteKey, inviteKey)
            .isNull(UserInviteKeyEntity::getDeleteTime)
            .last("limit 1")
            .one();
        if (entity == null) {
            throw new WebException(HttpStatus.BAD_REQUEST, "无效邀请码");
        }
        long userId = entity.getUserId();
        byte[] bs1 = ByteUtil.longToBytes(userId * 7);
        byte[] cp = compress(bs1);

        byte[] keyBytes = HexUtil.decodeHex(inviteKey);
        Assert.isTrue(keyBytes.length == 4);

        byte[] acp = new byte[2];
        byte[] split = magicSplit(keyBytes[0], keyBytes[1]);
        acp[0] = split[0];
        split = magicSplit(keyBytes[2], keyBytes[3]);
        acp[1] = split[0];

        if (!Arrays.equals(cp, acp)) {
            throw new WebException(HttpStatus.BAD_REQUEST, "无效邀请码");
        }
    }

    @Override
    public IndividualProfile getProfile(long userId) {
        UserAccountEntity userAccount = userAccountService.lambdaQuery()
            .select(UserAccountEntity::getId, UserAccountEntity::getUsername)
            .eq(UserAccountEntity::getId, userId)
            .eq(UserAccountEntity::getDeleted, false)
            .last("limit 1")
            .one();
        UserInfoEntity userInfo = userInfoService.lambdaQuery()
            .select(UserInfoEntity::getNickName, UserInfoEntity::getAvatar, UserInfoEntity::getHome,
                UserInfoEntity::getDescription, UserInfoEntity::getCreateTime)
            .eq(UserInfoEntity::getUserId, userId)
            .eq(UserInfoEntity::getDeleted, false)
            .last("limit 1")
            .one();
        if (ObjectUtil.hasNull(userAccount, userInfo)) {
            throw new BusinessException("没找到该用户信息");
        }
        IndividualProfile profile = new IndividualProfile();
        profile.setUid(String.valueOf(userAccount.getId()));
        profile.setUsername(userAccount.getUsername());
        profile.setNickName(userInfo.getNickName());
        profile.setAvatar(userInfo.getAvatar());
        profile.setHome(userInfo.getHome());
        profile.setDescription(userInfo.getDescription());
        profile.setCreateTime(DateUtil.toTimestamp(userInfo.getCreateTime()));
        return profile;
    }

    @Override
    public void updateProfile(long userId, IndividualProfileParam params) {
        String avatar = params.getAvatar();
        if (StrUtil.isBlank(avatar)) {
            avatar = randomDefaultAvatar();
        }
        String home = params.getHome();
        if (StrUtil.isBlank(params.getHome())) {
            home = "/home";
        }

        userInfoService.lambdaUpdate()
            .set(UserInfoEntity::getNickName, params.getNickName())
            .set(UserInfoEntity::getAvatar, avatar)
            .set(UserInfoEntity::getHome, home)
            .set(UserInfoEntity::getDescription, params.getDescription())
            .eq(UserInfoEntity::getUserId, userId)
            .eq(UserInfoEntity::getDeleted, false)
            .update();
    }
}
