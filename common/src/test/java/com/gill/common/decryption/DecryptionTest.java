package com.gill.common.decryption;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * DecryptionTest
 *
 * @author gill
 * @version 2024/01/29
 **/
public class DecryptionTest {

    @Test
    public void testDecryptionFactoryRegister() {
        final String decryptName = "mock";
        final String append = ":test";
        DecryptionFactory.register(new DecryptionStrategy() {
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
                return rawTest + append;
            }
        });
        DecryptionStrategy strategy = DecryptionFactory.getStrategy(decryptName);
        final String raw = "1234";
        Assertions.assertEquals(raw + append, strategy.decrypt(raw));
    }

    @Test
    public void testDecryptionFactoryGetStrategy() {
        DecryptionStrategy strategy = DecryptionFactory.getStrategy();
        final String raw = "1234";
        Assertions.assertEquals(raw, strategy.decrypt(raw));
    }

    @Test
    public void testDecryptionFactoryGetStrategyWithName() {
        DecryptionStrategy strategy = DecryptionFactory.getStrategy("DES");
        final String raw = "3579a554be5768c1";
        Assertions.assertEquals("123456", strategy.decrypt(raw));
    }

    @Test
    public void testDecryptionFactoryGetStrategyWithEmpty() {
        DecryptionStrategy strategy = DecryptionFactory.getStrategy("");
        final String raw = "1234";
        Assertions.assertEquals(raw, strategy.decrypt(raw));
    }

    @Test
    public void testDecryptionFactoryGetStrategyWithNull() {
        DecryptionStrategy strategy = DecryptionFactory.getStrategy(null);
        final String raw = "1234";
        Assertions.assertEquals(raw, strategy.decrypt(raw));
    }

    @Test
    public void testDecryptionFactoryGetStrategyWithUnsupported() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> DecryptionFactory.getStrategy("unsupported"));
    }

    @Test
    public void testDecryptionFactoryGetStrategyWithDefaultName() {
        DecryptionStrategy strategy = DecryptionFactory.getStrategy("default", "default");
        final String raw = "1234";
        Assertions.assertEquals(raw, strategy.decrypt(raw));
    }

    @Test
    public void testDecryptionFactoryGetStrategyWithDefaultName2() {
        DecryptionStrategy strategy = DecryptionFactory.getStrategy("unsupported", "default");
        final String raw = "1234";
        Assertions.assertEquals(raw, strategy.decrypt(raw));
    }

    @Test
    public void testDecryptionFactoryGetStrategyWithUnsupported2() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> DecryptionFactory.getStrategy("unsupported", "unsupported"));
    }
}
