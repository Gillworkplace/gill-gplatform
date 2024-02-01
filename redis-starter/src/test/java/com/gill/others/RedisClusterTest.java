package com.gill.others;

import com.gill.redis.core.Redis;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.embedded.RedisShardedCluster;

/**
 * RedisTest
 *
 * @author gill
 * @version 2024/01/29
 **/
@SpringBootTest(classes = TestApplication.class, properties = "spring.config.location=classpath:application-cluster.yaml")
public class RedisClusterTest {

    @Autowired
    private Redis redis;

    private static RedisShardedCluster cluster;

    /**
     * 构造方法之后执行.
     */
    @BeforeAll
    public static void startRedis() throws Exception {
        cluster = RedisShardedCluster.newRedisCluster()
            .serverPorts(List.of(11111, 11112, 11113))
            .shard("master1", 0)
            .shard("master2", 0)
            .shard("master3", 0)
            .build();
        cluster.start();
    }

    @AfterAll
    public static void stopRedis() throws IOException {
        cluster.stop();
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
        Assertions.assertDoesNotThrow(() -> redis.mset("map1", "key", "value"));
    }

    @Test
    public void testSetMap2() {
        Bean bean = new Bean();
        bean.setName("zzy");
        bean.setAge(18);
        Map<String, Object> map = new HashMap<>();
        map.put("k1", "v1");
        map.put("k2", bean);
        Assertions.assertDoesNotThrow(() -> redis.mset("map2", map));
    }

    @Test
    public void testSetMap3() {
        Assertions.assertDoesNotThrow(() -> redis.mset("map3", new HashMap<>()));
    }

    @Test
    public void testGetMap1() {
        redis.mset("map4", "key", "value");
        Assertions.assertEquals("value", redis.mget("map4", "key"));
    }

    @Test
    public void testGetMap2() {
        Bean bean = new Bean();
        bean.setName("zzy");
        bean.setAge(18);
        redis.mset("map5", "key", bean);
        Assertions.assertEquals(bean, redis.mget("map5", "key", Bean.class));
    }

    @Test
    public void testGetMap3() {
        redis.mset("map6", "key", "value");
        Assertions.assertEquals(1, redis.mget("map6").size());
    }

    @Test
    public void testGetMap4() {
        Bean bean = new Bean();
        bean.setName("zzy");
        bean.setAge(18);
        redis.mset("map7", "key", bean);
        Assertions.assertEquals(1, redis.mget("map7", Bean.class).size());
    }

    @Test
    public void testGetMap5() {
        redis.mset("map8", "k1", "v1");
        redis.mset("map8", "k2", "v2");
        Assertions.assertEquals(2, redis.mget("map8", Set.of("k1", "k2")).size());
    }

    @Test
    public void testGetMap6() {
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
}
