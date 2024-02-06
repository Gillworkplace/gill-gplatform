package com.gill.common.crypto;

/**
 * DecryptionStrategy
 *
 * @author gill
 * @version 2023/12/18
 **/
public interface CryptoStrategy {

    /**
     * 获取名称
     *
     * @return 名称
     */
    String getName();

    /**
     * 解密
     *
     * @param rawTest 明文
     * @return 密文
     */
    String decrypt(String rawTest);

    /**
     * 加密
     *
     * @param rawTest 明文
     * @return 密文
     */
    String encrypt(String rawTest);
}
