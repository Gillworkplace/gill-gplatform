package com.gill.others;

import com.gill.redis.core.Redis;
import java.io.IOException;
import java.util.Arrays;
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

    @Test
    public void test_llen() {
        redis.lrpush("list0", "1", "2");
        Assertions.assertEquals(2, redis.llen("list0"));
    }

    @Test
    public void test_lindex1() {
        redis.lrpush("list1", "1", "2");
        Assertions.assertEquals("2", redis.lindex("list1", 1));
    }

    @Test
    public void test_lindex2() {
        Bean bean1 = new Bean();
        bean1.setName("zzy");
        bean1.setAge(18);
        Bean bean2 = new Bean();
        bean2.setName("zzzy");
        bean2.setAge(19);
        redis.lrpush("list2", Bean.class, Arrays.asList(bean1, bean2));
        Assertions.assertEquals(bean2, redis.lindex("list2", 1, Bean.class));
    }


    @Test
    public void test_lpop1() {
        redis.lrpush("list3", "1", "2");
        Assertions.assertEquals(2, redis.llen("list3"));
        List<String> list3 = redis.lpop("list3", 2);
        Assertions.assertEquals("1", list3.get(0));
        Assertions.assertEquals("2", list3.get(1));
        Assertions.assertEquals(0, redis.llen("list3"));
    }

    @Test
    public void test_lpop2() {
        Bean bean1 = new Bean();
        bean1.setName("zzy");
        bean1.setAge(18);
        Bean bean2 = new Bean();
        bean2.setName("zzzy");
        bean2.setAge(19);
        redis.lrpush("list4", Bean.class, Arrays.asList(bean1, bean2));
        Assertions.assertEquals(2, redis.llen("list4"));
        List<Bean> list4 = redis.lpop("list4", Bean.class, 2);
        Assertions.assertEquals(bean1, list4.get(0));
        Assertions.assertEquals(bean2, list4.get(1));
        Assertions.assertEquals(0, redis.llen("list4"));
    }

    @Test
    public void test_lrpop1() {
        redis.lrpush("list5", "1", "2");
        Assertions.assertEquals(2, redis.llen("list5"));
        List<String> list5 = redis.lrpop("list5", 2);
        Assertions.assertEquals("2", list5.get(0));
        Assertions.assertEquals("1", list5.get(1));
        Assertions.assertEquals(0, redis.llen("list5"));
    }

    @Test
    public void test_lrpop2() {
        Bean bean1 = new Bean();
        bean1.setName("zzy");
        bean1.setAge(18);
        Bean bean2 = new Bean();
        bean2.setName("zzzy");
        bean2.setAge(19);
        redis.lrpush("list6", Bean.class, Arrays.asList(bean1, bean2));
        Assertions.assertEquals(2, redis.llen("list6"));
        List<Bean> list6 = redis.lrpop("list6", Bean.class, 2);
        Assertions.assertEquals(bean2, list6.get(0));
        Assertions.assertEquals(bean1, list6.get(1));
        Assertions.assertEquals(0, redis.llen("list6"));
    }

    @Test
    public void test_lpush1() {
        Assertions.assertDoesNotThrow(() -> redis.lpush("list7", "1", "2"));
        Assertions.assertEquals(2, redis.llen("list7"));
    }

    @Test
    public void test_lpush2() {
        Assertions.assertDoesNotThrow(() -> redis.lpush("list8", Arrays.asList("1", "2")));
        Assertions.assertEquals(2, redis.llen("list8"));
    }

    @Test
    public void test_lpush3() {
        Bean bean1 = new Bean();
        bean1.setName("zzy");
        bean1.setAge(18);
        Bean bean2 = new Bean();
        bean2.setName("zzzy");
        bean2.setAge(19);
        Assertions.assertDoesNotThrow(
            () -> redis.lpush("list9", Bean.class, Arrays.asList(bean1, bean2)));
        Assertions.assertEquals(2, redis.llen("list9"));
    }

    @Test
    public void test_lrpush1() {
        Assertions.assertDoesNotThrow(() -> redis.lrpush("list10", "1", "2"));
        Assertions.assertEquals(2, redis.llen("list10"));
    }

    @Test
    public void test_lrpush2() {
        Assertions.assertDoesNotThrow(() -> redis.lrpush("list11", Arrays.asList("1", "2")));
        Assertions.assertEquals(2, redis.llen("list11"));
    }

    @Test
    public void test_lrpush3() {
        Bean bean1 = new Bean();
        bean1.setName("zzy");
        bean1.setAge(18);
        Bean bean2 = new Bean();
        bean2.setName("zzzy");
        bean2.setAge(19);
        Assertions.assertDoesNotThrow(
            () -> redis.lrpush("list12", Bean.class, Arrays.asList(bean1, bean2)));
        Assertions.assertEquals(2, redis.llen("list12"));
    }

    @Test
    public void test_lrange1() {
        redis.lrpush("list13", "1", "2");
        List<String> list13 = redis.lrange("list13", 0, -1);
        Assertions.assertEquals("1", list13.get(0));
        Assertions.assertEquals("2", list13.get(1));
    }

    @Test
    public void test_lrange2() {
        Bean bean1 = new Bean();
        bean1.setName("zzy");
        bean1.setAge(18);
        Bean bean2 = new Bean();
        bean2.setName("zzzy");
        bean2.setAge(19);
        redis.lrpush("list14", Bean.class, Arrays.asList(bean1, bean2));
        List<Bean> list14 = redis.lrange("list14", 0, -1, Bean.class);
        Assertions.assertEquals(bean1, list14.get(0));
        Assertions.assertEquals(bean2, list14.get(1));
    }

    @Test
    public void test_lset1() {
        redis.lrpush("list15", "1", "2");
        redis.lset("list15", 1, "3");
        Assertions.assertEquals("3", redis.lindex("list15", 1));
    }

    @Test
    public void test_lset2() {
        Bean bean1 = new Bean();
        bean1.setName("zzy");
        bean1.setAge(18);
        Bean bean2 = new Bean();
        bean2.setName("zzzy");
        bean2.setAge(19);
        Bean bean3 = new Bean();
        bean3.setName("zzzzy");
        bean3.setAge(20);
        redis.lrpush("list16", Bean.class, Arrays.asList(bean1, bean2));
        redis.lset("list16", 1, bean3, Bean.class);
        Assertions.assertEquals(bean3, redis.lindex("list16", 1, Bean.class));
    }
}
