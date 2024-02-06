package com.gill.common.crypto;

import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.Field;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * CryptoTest
 *
 * @author gill
 * @version 2024/01/29
 **/
public class CryptoTest {

    @Test
    public void testCryptoFactoryRegister() {
        final String decryptName = "mock";
        final String append = ":test";
        CryptoFactory.register(new CryptoStrategy() {
            /**
             * 获取名称
             *
             * @return 名称
             */
            @Override
            public String getName() {
                return decryptName;
            }

            /**
             * 解密
             *
             * @param rawTest 明文
             * @return 密文
             */
            @Override
            public String decrypt(String rawTest) {
                return rawTest.substring(0, rawTest.length() - append.length());
            }

            /**
             * 加密
             *
             * @param rawTest 明文
             * @return 密文
             */
            @Override
            public String encrypt(String rawTest) {
                return rawTest + append;
            }
        });
        Assertions.assertNotNull(CryptoFactory.getStrategy(decryptName));
    }

    @Test
    public void testAllCryptoStrategy() {
        Field field = ReflectUtil.getField(CryptoFactory.class, "STRATEGIES");
        @SuppressWarnings({
            "unchecked"}) Map<String, CryptoStrategy> strategies = (Map<String, CryptoStrategy>) ReflectUtil.getStaticFieldValue(
            field);
        final String raw = "1234";
        for (CryptoStrategy strategy : strategies.values()) {
            String encrypt = strategy.encrypt(raw);
            try {
                String decrypt = strategy.decrypt(encrypt);
                Assertions.assertEquals(raw, decrypt, "strategy: " + strategy.getName());
            } catch (UnsupportedOperationException ignored) {
            }
        }
    }

    @Test
    public void testCryptoFactoryGetStrategy() {
        CryptoStrategy strategy = CryptoFactory.getStrategy();
        final String raw = "1234";
        Assertions.assertEquals(raw, strategy.decrypt(raw));
    }

    @Test
    public void testCryptoFactoryGetStrategyWithName() {
        CryptoStrategy strategy = CryptoFactory.getStrategy("DES");
        final String raw = "3579a554be5768c1";
        Assertions.assertEquals("123456", strategy.decrypt(raw));
    }

    @Test
    public void testCryptoFactoryGetStrategyWithEmpty() {
        CryptoStrategy strategy = CryptoFactory.getStrategy("");
        final String raw = "1234";
        Assertions.assertEquals(raw, strategy.decrypt(raw));
    }


    @Test
    public void testCryptoFactoryGetStrategyWithNull() {
        CryptoStrategy strategy = CryptoFactory.getStrategy(null);
        final String raw = "1234";
        Assertions.assertEquals(raw, strategy.decrypt(raw));
    }

    @Test
    public void testCryptoFactoryGetStrategyWithUnsupported() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> CryptoFactory.getStrategy("unsupported"));
    }

    @Test
    public void testCryptoFactoryGetStrategyWithDefaultName() {
        CryptoStrategy strategy = CryptoFactory.getStrategy("default", "default");
        final String raw = "1234";
        Assertions.assertEquals(raw, strategy.decrypt(raw));
    }

    @Test
    public void testCryptoFactoryGetStrategyWithDefaultName2() {
        CryptoStrategy strategy = CryptoFactory.getStrategy("unsupported", "default");
        final String raw = "1234";
        Assertions.assertEquals(raw, strategy.decrypt(raw));
    }

    @Test
    public void testCryptoFactoryGetStrategyWithUnsupported2() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> CryptoFactory.getStrategy("unsupported", "unsupported"));
    }
}
