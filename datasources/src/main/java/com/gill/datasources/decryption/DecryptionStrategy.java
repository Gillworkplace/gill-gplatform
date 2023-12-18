package com.gill.datasources.decryption;

/**
 * DecryptionStrategy
 *
 * @author gill
 * @version 2023/12/18
 **/
public interface DecryptionStrategy {

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
}
