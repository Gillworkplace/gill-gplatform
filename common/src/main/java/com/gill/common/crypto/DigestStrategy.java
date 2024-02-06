package com.gill.common.crypto;

/**
 * DigestStrategy
 *
 * @author gill
 * @version 2024/02/06
 **/
public interface DigestStrategy extends CryptoStrategy {

    default String decrypt(String rawTest) {
        throw new UnsupportedOperationException(this.getName() + " unsupport decrypt operation");
    }
}
