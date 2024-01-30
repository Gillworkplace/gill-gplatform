package com.gill.redis.config;


import com.gill.common.decryption.DecryptionFactory;
import com.gill.common.decryption.DecryptionStrategy;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.ClientType;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Cluster;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Lettuce;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Pool;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * RedisConfig
 *
 * @author gill
 * @version 2024/01/29
 **/
@EnableConfigurationProperties({AliasRedisProperties.class})
public class BeforeRedisConfiguration {

    /**
     * 自定义redis properties 转 springboot data redis 的 redis properties
     *
     * @param aliasRedisProperties 自定义redis properties
     * @return redis properties
     */
    @Primary
    @Bean
    public RedisProperties redisProperties(AliasRedisProperties aliasRedisProperties) {
        DecryptionStrategy strategy = DecryptionFactory.getStrategy(
            aliasRedisProperties.getDecryptionName());
        RedisProperties redisProperties = new RedisProperties();
        setNodes(aliasRedisProperties.getNodes(), redisProperties);
        redisProperties.setDatabase(aliasRedisProperties.getDatabase());
        redisProperties.setPassword(strategy.decrypt(aliasRedisProperties.getPassword()));
        redisProperties.setClientType(ClientType.LETTUCE);
        setPool(aliasRedisProperties, redisProperties);
        return redisProperties;
    }

    private static void setNodes(List<String> nodes, RedisProperties redisProperties) {
        if (nodes.size() == 1) {
            String hostPort = nodes.get(0);
            String[] split = hostPort.split(":");
            if (split.length >= 1) {
                redisProperties.setHost(split[0]);
            }
            if (split.length >= 2) {
                redisProperties.setPort(Integer.parseInt(split[1]));
            }
        } else {
            Cluster cluster = new Cluster();
            cluster.setNodes(nodes);
            redisProperties.setCluster(cluster);
        }
    }

    private static void setPool(AliasRedisProperties aliasRedisProperties,
        RedisProperties redisProperties) {
        Lettuce lettuce = redisProperties.getLettuce();
        Pool pool = lettuce.getPool();
        pool.setMaxActive(aliasRedisProperties.getMaxActive());
        pool.setMaxIdle(aliasRedisProperties.getMaxIdle());
        pool.setMinIdle(aliasRedisProperties.getMinIdle());
        pool.setMaxWait(Duration.ofMillis(aliasRedisProperties.getMaxWait()));
    }
}
