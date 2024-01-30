package com.gill.others;

import com.gill.redis.core.Redis;
import java.io.IOException;
import java.util.List;
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
}
