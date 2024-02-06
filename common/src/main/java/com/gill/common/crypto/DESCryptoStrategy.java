package com.gill.common.crypto;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import java.nio.charset.StandardCharsets;

/**
 * SymmetryDecryptionStrategy
 *
 * @author gill
 * @version 2024/01/29
 **/
public class DESCryptoStrategy implements CryptoStrategy {

    private static final String KEY = System.getenv("DES_KEY");

    private static final String DEFAULT_KEY = "jPQQdFt3LgW=";

    private final SymmetricCrypto crypto = SecureUtil.des(
        (KEY == null ? DEFAULT_KEY : KEY).getBytes(StandardCharsets.UTF_8));

    /**
     * 获取名称
     *
     * @return 名称
     */
    @Override
    public String getName() {
        return "DES";
    }

    /**
     * 解密
     *
     * @param rawTest 明文
     * @return 密文
     */
    @Override
    public String decrypt(String rawTest) {
        return crypto.decryptStr(HexUtil.decodeHex(rawTest), StandardCharsets.UTF_8);
    }

    /**
     * 加密
     *
     * @param rawTest 明文
     * @return 密文
     */
    @Override
    public String encrypt(String rawTest) {
        return crypto.encryptHex(rawTest, StandardCharsets.UTF_8);
    }
}
