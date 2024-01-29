package com.gill.common.decryption;

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
public class DecryptionFactory {

    private static final Map<String, DecryptionStrategy> STRATEGIES = new ConcurrentHashMap<>(16);

    static {
        loadStrategy();
    }

    private static void loadStrategy() {
        DecryptionStrategy defaultStrategy = new DefaultDecryptionStrategy();
        STRATEGIES.put(defaultStrategy.getName(), defaultStrategy);
        ServiceLoader<DecryptionStrategy> loader = ServiceLoader.load(DecryptionStrategy.class);
        for (DecryptionStrategy strategy : loader) {
            register(strategy);
        }
    }

    /**
     * 注册解密策略
     *
     * @param strategy 策略
     */
    public static void register(DecryptionStrategy strategy) {
        STRATEGIES.put(strategy.getName(), strategy);
    }

    /**
     * 获取解密策略
     *
     * @return 策略
     */
    public static DecryptionStrategy getStrategy() {
        DecryptionStrategy strategy = STRATEGIES.get("default");
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
    public static DecryptionStrategy getStrategy(String name) {
        name = Strings.isNullOrEmpty(name) ? "default" : name;
        DecryptionStrategy strategy = STRATEGIES.get(name);
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
    public static DecryptionStrategy getStrategy(String name, String defaultStrategyName) {
        name = Strings.isNullOrEmpty(name) ? "default" : name;
        DecryptionStrategy strategy = STRATEGIES.get(name);
        if (strategy == null) {
            strategy = STRATEGIES.get(defaultStrategyName);
        }
        if (strategy == null) {
            throw new IllegalArgumentException("No such decryption strategy: " + name);
        }
        return strategy;
    }
}
