package com.gill.others;

import com.gill.redis.core.Redis;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.embedded.RedisServer;

/**
 * RedisTest
 *
 * @author gill
 * @version 2024/01/29
 **/
@SpringBootTest(classes = TestApplication.class, properties = "spring.config.location=classpath:application-singleton.yaml")
public class RedisSingletonTest {

    @Autowired
    private Redis redis;

    private static RedisServer redisServer;

    /**
     * 构造方法之后执行.
     */
    @BeforeAll
    public static void startRedis() throws IOException {
        redisServer = RedisServer.newRedisServer()
            .port(22222)
            .setting("bind 127.0.0.1")
            .setting("maxmemory 128M")
            .setting("requirepass 123456")
            .build();
        redisServer.start();
    }

    @AfterAll
    public static void stopRedis() throws IOException {
        redisServer.stop();
    }


    @Test
    public void test_set() {
        Assertions.assertDoesNotThrow(() -> redis.set("t", "123"));
    }

    @Test
    public void test_get1() {
        redis.set("test", "123");
        Assertions.assertEquals("123", redis.get("test"));
    }

    @Test
    public void test_get2() {
        Bean bean = new Bean();
        bean.setName("zzy");
        bean.setAge(18);
        redis.set("test1", bean);
        Assertions.assertEquals(bean, redis.get("test1", Bean.class));
    }

    @Test
    public void test_mset1() {
        Assertions.assertDoesNotThrow(() -> redis.mset("map1", "key", "value"));
    }

    @Test
    public void test_mset2() {
        Bean bean = new Bean();
        bean.setName("zzy");
        bean.setAge(18);
        Map<String, Object> map = new HashMap<>();
        map.put("k1", "v1");
        map.put("k2", bean);
        Assertions.assertDoesNotThrow(() -> redis.mset("map2", map));
    }

    @Test
    public void test_mset3() {
        Assertions.assertDoesNotThrow(() -> redis.mset("map3", new HashMap<>()));
    }

    @Test
    public void test_mget1() {
        redis.mset("map4", "key", "value");
        Assertions.assertEquals("value", redis.mget("map4", "key"));
    }

    @Test
    public void test_mget2() {
        Bean bean = new Bean();
        bean.setName("zzy");
        bean.setAge(18);
        redis.mset("map5", "key", bean);
        Assertions.assertEquals(bean, redis.mget("map5", "key", Bean.class));
    }

    @Test
    public void test_mget3() {
        redis.mset("map6", "key", "value");
        Assertions.assertEquals(1, redis.mget("map6").size());
    }

    @Test
    public void test_mget4() {
        Bean bean = new Bean();
        bean.setName("zzy");
        bean.setAge(18);
        redis.mset("map7", "key", bean);
        Assertions.assertEquals(1, redis.mget("map7", Bean.class).size());
    }

    @Test
    public void test_mget5() {
        redis.mset("map8", "k1", "v1");
        redis.mset("map8", "k2", "v2");
        Assertions.assertEquals(2, redis.mget("map8", Set.of("k1", "k2")).size());
    }

    @Test
    public void test_mget6() {
        Bean bean1 = new Bean();
        bean1.setName("zzy");
        bean1.setAge(18);
        Bean bean2 = new Bean();
        bean2.setName("zzzy");
        bean2.setAge(19);
        redis.mset("map9", "k1", bean1);
        redis.mset("map9", "k2", bean2);
        Assertions.assertEquals(2, redis.mget("map8", Set.of("k1", "k2"), Bean.class).size());
    }

    @Test
    public void test_sget() {
        redis.sadd("set1", "1");
        Set<String> set = redis.sget("set1");
        Assertions.assertEquals(1, set.size());
    }

    @Test
    public void test_sadd1() {
        long cnt1 = redis.sadd("set2", "1", "2");
        long cnt2 = redis.sadd("set2", "1", "3");
        Assertions.assertEquals(2, cnt1);
        Assertions.assertEquals(1, cnt2);
    }

    @Test
    public void test_sadd2() {
        long cnt1 = redis.sadd("set3", Arrays.asList("1", "2"));
        long cnt2 = redis.sadd("set3", Arrays.asList("1", "3"));
        Assertions.assertEquals(2, cnt1);
        Assertions.assertEquals(1, cnt2);
    }

    @Test
    public void test_sremove1() {
        redis.sadd("set4", "1", "2");
        long cnt1 = redis.sremove("set4", "1", "2");
        long cnt2 = redis.sremove("set4", "1", "3");
        Assertions.assertEquals(2, cnt1);
        Assertions.assertEquals(0, cnt2);
    }

    @Test
    public void test_sremove2() {
        redis.sadd("set5", "1", "2");
        long cnt1 = redis.sremove("set5", Arrays.asList("1", "2"));
        long cnt2 = redis.sremove("set5", Arrays.asList("1", "3"));
        Assertions.assertEquals(2, cnt1);
        Assertions.assertEquals(0, cnt2);
    }

    @Test
    public void test_sclear() {
        redis.sadd("set6", "1", "2");
        redis.sclear("set6");
        Set<String> set = redis.sget("set6");
        Assertions.assertEquals(0, set.size());
    }

    @Test
    public void test_scontains1() {
        redis.sadd("set7", "1", "2");
        Assertions.assertTrue(redis.scontains("set7", "1"));
        Assertions.assertFalse(redis.scontains("set7", "3"));
    }

    @Test
    public void test_scontains2() {
        redis.sadd("set8", Arrays.asList("1", "2"));
        Assertions.assertTrue(redis.scontains("set8", "1"));
        Assertions.assertFalse(redis.scontains("set8", "3"));
    }

    @Test
    public void test_scount() {
        redis.sadd("set9", Arrays.asList("1", "2"));
        Assertions.assertEquals(2, redis.scount("set9"));
    }
}
