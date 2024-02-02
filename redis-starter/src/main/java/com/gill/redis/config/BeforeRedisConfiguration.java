package com.gill.redis.config;


import com.gill.common.decryption.DecryptionFactory;
import com.gill.common.decryption.DecryptionStrategy;
import com.gill.common.util.ServerUtil;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.redisson.config.TransportMode;
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

    @Bean
    public RedissonClient redissonClient(AliasRedisProperties properties) {
        Config config = new Config();
        config.setThreads(ServerUtil.getCpuCores() * 2 + 1)
            .setNettyThreads(ServerUtil.getCpuCores() * 2 + 1)
            .setTransportMode(TransportMode.NIO)
            .setCodec(new org.redisson.codec.JsonJacksonCodec());
        if (isCluster(properties)) {
            buildClusterConfig(config, properties);
        } else {
            buildSingletonConfig(config, properties);
        }
        return Redisson.create(config);
    }

    private boolean isCluster(AliasRedisProperties aliasRedisProperties) {
        List<String> nodes = aliasRedisProperties.getNodes();
        return Optional.ofNullable(nodes).map(list -> list.size() > 1).orElse(false);
    }

    private void buildSingletonConfig(Config config, AliasRedisProperties properties) {
        DecryptionStrategy strategy = DecryptionFactory.getStrategy(properties.getDecryptionName());
        config.useSingleServer()
            .setAddress("redis://" + properties.getNodes().get(0))
            .setPassword(strategy.decrypt(properties.getPassword()))
            .setDatabase(properties.getDatabase())
            .setConnectionMinimumIdleSize(Math.max(1, properties.getMinIdle()))
            .setConnectionPoolSize(properties.getMaxActive())
            .setSubscriptionConnectionPoolSize(50)
            .setSubscriptionConnectionMinimumIdleSize(1)
            .setSubscriptionsPerConnection(5)
            .setTimeout(3000)
            .setConnectTimeout(10000)
            .setIdleConnectionTimeout(10000)
            .setRetryAttempts(3)
            .setRetryInterval(1500);
    }

    private void buildClusterConfig(Config config, AliasRedisProperties properties) {
        DecryptionStrategy strategy = DecryptionFactory.getStrategy(properties.getDecryptionName());
        List<String> nodes = properties.getNodes().stream().map(node -> "redis://" + node).toList();
        config.useClusterServers()
            .setMasterConnectionMinimumIdleSize(Math.max(1, properties.getMinIdle()))
            .setMasterConnectionPoolSize(properties.getMaxActive())
            .setSlaveConnectionMinimumIdleSize(Math.max(1, properties.getMinIdle()))
            .setSlaveConnectionPoolSize(properties.getMaxActive())
            .setSubscriptionConnectionPoolSize(50)
            .setSubscriptionConnectionMinimumIdleSize(1)
            .setSubscriptionsPerConnection(5)
            .setLoadBalancer(new org.redisson.connection.balancer.CommandsLoadBalancer())
            .setTimeout(3000)
            .setConnectTimeout(10000)
            .setIdleConnectionTimeout(10000)
            .setRetryAttempts(3)
            .setRetryInterval(1500)
            .setSubscriptionMode(SubscriptionMode.MASTER)
            .setReadMode(ReadMode.MASTER)
            .setPassword(strategy.decrypt(properties.getPassword()))
            .setNodeAddresses(nodes);
    }
}
