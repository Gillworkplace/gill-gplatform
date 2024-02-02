package com.gill.redis.core;

import com.gill.common.api.DLock;
import com.gill.common.exception.ExceptionUtil;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * RedisLock
 *
 * @author gill
 * @version 2024/02/01
 **/
@Slf4j
public class RedissonDLockAdapter implements DLock {

    private static final long MAX_WAIT_TIME = 5L * 60 * 1000;

    private static final long DEFAULT_TRY_LOCK_TIME = 100L;

    private static final long LOCK_EXPIRED = 30L * 1000;

    private final RedissonClient client;

    public RedissonDLockAdapter(RedissonClient client) {
        this.client = client;
    }

    /**
     * 加锁
     *
     * @param key key
     * @return true: 加锁成功; false: 加锁失败
     */
    @Override
    public boolean lock(String key) {
        return lock(key, MAX_WAIT_TIME);
    }

    /**
     * 加锁
     *
     * @param key     key
     * @param timeout 等待超时时间
     * @return true: 加锁成功; false: 加锁失败
     */
    @Override
    public boolean lock(String key, long timeout) {
        RLock lock = client.getLock(key);
        try {
            return lock.tryLock(timeout, LOCK_EXPIRED, TimeUnit.MICROSECONDS);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 尝试加锁
     *
     * @param key key
     * @return true: 加锁成功; false: 加锁失败
     */
    @Override
    public boolean tryLock(String key) {
        return lock(key, DEFAULT_TRY_LOCK_TIME);
    }

    /**
     * 解锁
     *
     * @param key key
     */
    @Override
    public void unlock(String key) {
        RLock lock = client.getLock(key);
        try {
            lock.unlock();
        } catch (Exception e) {
            log.error("unlock {} error, e: {}", key, ExceptionUtil.getAllMessage(e));
        }
    }
}
