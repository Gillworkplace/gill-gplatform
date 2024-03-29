package com.gill.common.crypto;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * Sha256DigestStrategy
 *
 * @author gill
 * @version 2024/02/06
 **/
public class Md5DigestStrategy implements DigestStrategy {

    /**
     * 获取名称
     *
     * @return 名称
     */
    @Override
    public String getName() {
        return "md5";
    }

    /**
     * 加密
     *
     * @param rawTest 明文
     * @return 密文
     */
    @Override
    public String encrypt(String rawTest) {
        return DigestUtil.md5Hex(rawTest);
    }
}
