package com.gill.common.crypto;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * Sha256DigestStrategy
 *
 * @author gill
 * @version 2024/02/06
 **/
public class Sha512DigestStrategy implements DigestStrategy {

    /**
     * 获取名称
     *
     * @return 名称
     */
    @Override
    public String getName() {
        return "sha512";
    }

    /**
     * 加密
     *
     * @param rawTest 明文
     * @return 密文
     */
    @Override
    public String encrypt(String rawTest) {
        return DigestUtil.sha512Hex(rawTest);
    }
}
