package com.gill.datasource.decryption;

import com.gill.common.crypto.CryptoStrategy;

/**
 * MockDecryptionStrategy
 *
 * @author gill
 * @version 2023/12/18
 **/
public class MockCryptoStrategy implements CryptoStrategy {

    /**
     * 获取名称
     *
     * @return 名称
     */
    @Override
    public String getName() {
        return "mock";
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
}
