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
    public void testSet() {
        Assertions.assertDoesNotThrow(() -> redis.set("t", "123"));
    }

    @Test
    public void testGet1() {
        redis.set("test", "123");
        Assertions.assertEquals("123", redis.get("test"));
    }

    @Test
    public void testGet2() {
        Bean bean = new Bean();
        bean.setName("zzy");
        bean.setAge(18);
        redis.set("test1", bean);
        Assertions.assertEquals(bean, redis.get("test1", Bean.class));
    }

    @Test
    public void testSetMap1() {
        Assertions.assertDoesNotThrow(() -> redis.setMap("map1", "key", "value"));
    }

    @Test
    public void testSetMap2() {
        Bean bean = new Bean();
        bean.setName("zzy");
        bean.setAge(18);
        Map<String, Object> map = new HashMap<>();
        map.put("k1", "v1");
        map.put("k2", bean);
        Assertions.assertDoesNotThrow(() -> redis.setMap("map2", map));
    }

    @Test
    public void testSetMap3() {
        Assertions.assertDoesNotThrow(() -> redis.setMap("map3", new HashMap<>()));
    }

    @Test
    public void testGetMap1() {
        redis.setMap("map4", "key", "value");
        Assertions.assertEquals("value", redis.getMap("map4", "key"));
    }

    @Test
    public void testGetMap2() {
        Bean bean = new Bean();
        bean.setName("zzy");
        bean.setAge(18);
        redis.setMap("map5", "key", bean);
        Assertions.assertEquals(bean, redis.getMap("map5", "key", Bean.class));
    }

    @Test
    public void testGetMap3() {
        redis.setMap("map6", "key", "value");
        Assertions.assertEquals(1, redis.getMap("map6").size());
    }

    @Test
    public void testGetMap4() {
        Bean bean = new Bean();
        bean.setName("zzy");
        bean.setAge(18);
        redis.setMap("map7", "key", bean);
        Assertions.assertEquals(1, redis.getMap("map7", Bean.class).size());
    }

    @Test
    public void testGetMap5() {
        redis.setMap("map8", "k1", "v1");
        redis.setMap("map8", "k2", "v2");
        Assertions.assertEquals(2, redis.getMap("map8", Set.of("k1", "k2")).size());
    }

    @Test
    public void testGetMap6() {
        Bean bean1 = new Bean();
        bean1.setName("zzy");
        bean1.setAge(18);
        Bean bean2 = new Bean();
        bean2.setName("zzzy");
        bean2.setAge(19);
        redis.setMap("map9", "k1", bean1);
        redis.setMap("map9", "k2", bean2);
        Assertions.assertEquals(2, redis.getMap("map8", Set.of("k1", "k2"), Bean.class).size());
    }

    @Test
    public void testGetSet() {
        redis.addSet("set1", "1");
        Set<String> set = redis.getSet("set1");
        Assertions.assertEquals(1, set.size());
    }

    @Test
    public void testAddSet1() {
        long cnt1 = redis.addSet("set2", "1", "2");
        long cnt2 = redis.addSet("set2", "1", "3");
        Assertions.assertEquals(2, cnt1);
        Assertions.assertEquals(1, cnt2);
    }

    @Test
    public void testAddSet2() {
        long cnt1 = redis.addSet("set3", Arrays.asList("1", "2"));
        long cnt2 = redis.addSet("set3", Arrays.asList("1", "3"));
        Assertions.assertEquals(2, cnt1);
        Assertions.assertEquals(1, cnt2);
    }

    @Test
    public void testRemoveSet1() {
        redis.addSet("set4", "1", "2");
        long cnt1 = redis.removeSet("set4", "1", "2");
        long cnt2 = redis.removeSet("set4", "1", "3");
        Assertions.assertEquals(2, cnt1);
        Assertions.assertEquals(0, cnt2);
    }

    @Test
    public void testRemoveSet2() {
        redis.addSet("set5", "1", "2");
        long cnt1 = redis.removeSet("set5", Arrays.asList("1", "2"));
        long cnt2 = redis.removeSet("set5", Arrays.asList("1", "3"));
        Assertions.assertEquals(2, cnt1);
        Assertions.assertEquals(0, cnt2);
    }

    @Test
    public void testClearSet() {
        redis.addSet("set6", "1", "2");
        redis.clearSet("set6");
        Set<String> set = redis.getSet("set6");
        Assertions.assertEquals(0, set.size());
    }

    @Test
    public void testContains1() {
        redis.addSet("set7", "1", "2");
        Assertions.assertTrue(redis.contains("set7", "1"));
        Assertions.assertFalse(redis.contains("set7", "3"));
    }

    @Test
    public void testContains2() {
        redis.addSet("set8", Arrays.asList("1", "2"));
        Assertions.assertTrue(redis.contains("set8", "1"));
        Assertions.assertFalse(redis.contains("set8", "3"));
    }
}
