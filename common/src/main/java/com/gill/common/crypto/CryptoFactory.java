package com.gill.common.crypto;

import com.google.common.base.Strings;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DecryptionFactory
 *
 * @author gill
 * @version 2023/12/18
 **/
public class CryptoFactory {

    private static final Map<String, CryptoStrategy> STRATEGIES = new ConcurrentHashMap<>(16);

    static {
        loadStrategy();
    }

    private static void loadStrategy() {
        loadLocalStrategy();
        ServiceLoader<CryptoStrategy> loader = ServiceLoader.load(CryptoStrategy.class);
        for (CryptoStrategy strategy : loader) {
            register(strategy);
        }
    }

    private static void loadLocalStrategy() {
        register(new DefaultCryptoStrategy());
        register(new DESCryptoStrategy());
        register(new Md5DigestStrategy());
        register(new Sha256DigestStrategy());
        register(new Sha512DigestStrategy());
    }

    /**
     * 注册解密策略
     *
     * @param strategy 策略
     */
    public static void register(CryptoStrategy strategy) {
        STRATEGIES.put(strategy.getName(), strategy);
    }

    /**
     * 获取解密策略
     *
     * @return 策略
     */
    public static CryptoStrategy getStrategy() {
        CryptoStrategy strategy = STRATEGIES.get("default");
        if (strategy == null) {
            throw new IllegalArgumentException("No such decryption strategy: default");
        }
        return strategy;
    }

    /**
     * 获取解密策略
     *
     * @param name name
     * @return 策略
     */
    public static CryptoStrategy getStrategy(String name) {
        name = Strings.isNullOrEmpty(name) ? "default" : name;
        CryptoStrategy strategy = STRATEGIES.get(name);
        if (strategy == null) {
            throw new IllegalArgumentException("No such decryption strategy: " + name);
        }
        return strategy;
    }

    /**
     * 获取解密策略
     *
     * @param name                name
     * @param defaultStrategyName 默认解密策略
     * @return 策略
     */
    public static CryptoStrategy getStrategy(String name, String defaultStrategyName) {
        name = Strings.isNullOrEmpty(name) ? "default" : name;
        CryptoStrategy strategy = STRATEGIES.get(name);
        if (strategy == null) {
            strategy = STRATEGIES.get(defaultStrategyName);
        }
        if (strategy == null) {
            throw new IllegalArgumentException("No such decryption strategy: " + name);
        }
        return strategy;
    }
}
