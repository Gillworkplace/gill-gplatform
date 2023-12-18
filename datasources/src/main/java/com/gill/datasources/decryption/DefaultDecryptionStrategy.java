package com.gill.datasources.decryption;

/**
 * DefaultDecryptionStrategy
 *
 * @author gill
 * @version 2023/12/18
 **/
public class DefaultDecryptionStrategy implements DecryptionStrategy {

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
}
