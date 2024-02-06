package com.gill.common.crypto;

/**
 * DefaultDecryptionStrategy
 *
 * @author gill
 * @version 2023/12/18
 **/
public class DefaultCryptoStrategy implements CryptoStrategy {

    /**
     * 获取名称
     *
     * @return 名称
     */
    @Override
    public String getName() {
        return "default";
    }

    /**
     * 解密
     *
     * @param rawTest 明文
     * @return 密文
     */
    @Override
    public String decrypt(String rawTest) {
        return rawTest;
    }

    /**
     * 加密
     *
     * @param rawTest 明文
     * @return 密文
     */
    @Override
    public String encrypt(String rawTest) {
        return rawTest;
    }
}
