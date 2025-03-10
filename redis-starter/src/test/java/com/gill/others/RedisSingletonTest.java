package com.gill.others;

import cn.hutool.core.thread.ThreadUtil;
import com.gill.common.api.DLock;
import com.gill.redis.core.Redis;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    private DLock lock;

    private static RedisServer redisServer;

    /**
     * 构造方法之后执行.
     */
    @BeforeAll
    public static void startRedis() throws Exception {
        redisServer = RedisServer.newRedisServer()
            .port(22222)
            .setting("bind 127.0.0.1")
            .setting("maxmemory 128M")
            .setting("requirepass 123456")
            .build();
        redisServer.start();
    }

    @AfterAll
    public static void stopRedis() throws Exception {
        redisServer.stop();
    }

    @Test
    public void test_lock_unlock_success() {
        Assertions.assertTrue(lock.lock("lock1"));
        lock.unlock("lock1");
    }

    @Test
    public void test_lock_failed() throws Exception {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Thread async = new Thread(() -> {
            lock.lock("lock2");
            future.complete(null);
        });
        async.start();
        future.get(1000L, TimeUnit.MILLISECONDS);
        Assertions.assertFalse(lock.lock("lock2", 1000L));
    }

    @Test
    public void test_tryLock_unlock_success() {
        Assertions.assertTrue(lock.tryLock("lock3"));
        lock.unlock("lock3");
    }

    @Test
    public void test_tryLock_failed() throws Exception {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Thread async = new Thread(() -> {
            lock.lock("lock4");
            future.complete(null);
        });
        async.start();
        future.get(1000L, TimeUnit.MILLISECONDS);
        Assertions.assertFalse(lock.tryLock("lock4"));
    }

    @Test
    public void test_add() {
        Assertions.assertEquals(1L, redis.increaseAndGet("calc"));
        redis.set("calc", 0);
        Assertions.assertEquals(1L, redis.increaseAndGet("calc"));
        Assertions.assertEquals(0L, redis.decreaseAndGet("calc"));
        Assertions.assertEquals(5L, redis.addAndGet("calc", 5));
    }

    @Test
    public void test_clear() {
        redis.set("clear", "1");
        redis.clear("clear");
        Assertions.assertEquals("", redis.get("clear"));
    }

    @Test
    public void test_set1() {
        Assertions.assertDoesNotThrow(() -> redis.set("t1", "123"));
    }

    @Test
    public void test_set2() {
        redis.set("t2", "123", 1000L);
        Assertions.assertEquals("123", redis.get("t2"));
        ThreadUtil.sleep(1000);
        Assertions.assertEquals("", redis.get("t2"));
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
    public void test_hset1() {
        Assertions.assertDoesNotThrow(() -> redis.hset("map1", "key", "value"));
    }

    @Test
    public void test_hset2() {
        Bean bean = new Bean();
        bean.setName("zzy");
        bean.setAge(18);
        Map<String, Object> map = new HashMap<>();
        map.put("k1", "v1");
        map.put("k2", bean);
        Assertions.assertDoesNotThrow(() -> redis.hset("map2", map));
    }

    @Test
    public void test_hset3() {
        Assertions.assertDoesNotThrow(() -> redis.hset("map3", new HashMap<>()));
    }

    @Test
    public void test_hget1() {
        redis.hset("map4", "key", "value");
        Assertions.assertEquals("value", redis.hget("map4", "key"));
    }

    @Test
    public void test_hget2() {
        Bean bean = new Bean();
        bean.setName("zzy");
        bean.setAge(18);
        redis.hset("map5", "key", bean);
        Assertions.assertEquals(bean, redis.hget("map5", "key", Bean.class));
    }

    @Test
    public void test_hget3() {
        redis.hset("map6", "key", "value");
        Assertions.assertEquals(1, redis.hget("map6").size());
    }

    @Test
    public void test_hget4() {
        Bean bean = new Bean();
        bean.setName("zzy");
        bean.setAge(18);
        redis.hset("map7", "key", bean);
        Assertions.assertEquals(1, redis.hget("map7", Bean.class).size());
    }

    @Test
    public void test_hget5() {
        redis.hset("map8", "k1", "v1");
        redis.hset("map8", "k2", "v2");
        Assertions.assertEquals(2, redis.hget("map8", Set.of("k1", "k2")).size());
    }

    @Test
    public void test_hget6() {
        Bean bean1 = new Bean();
        bean1.setName("zzy");
        bean1.setAge(18);
        Bean bean2 = new Bean();
        bean2.setName("zzzy");
        bean2.setAge(19);
        redis.hset("map9", "k1", bean1);
        redis.hset("map9", "k2", bean2);
        Assertions.assertEquals(2, redis.hget("map9", Set.of("k1", "k2"), Bean.class).size());
    }

    @Test
    public void test_hsetEx() {
        redis.hset("map10", Map.of("test", "test"), 10 * 1000L);
        Assertions.assertTrue(redis.ttl("map10") > 0);
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

    @Test
    public void test_saddEx() {
        redis.sadd("set10", List.of("test"), 10 * 1000L);
        Assertions.assertTrue(redis.ttl("set10") > 0);
    }
}
