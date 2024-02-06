package com.gill.common.api;

/**
 * Lock
 *
 * @author gill
 * @version 2024/02/01
 **/
public interface DLock {

    /**
     * 加锁
     *
     * @param key key
     * @return true: 加锁成功; false: 加锁失败
     */
    boolean lock(String key);

    /**
     * 加锁
     *
     * @param key     key
     * @param timeout 等待超时时间
     * @return true: 加锁成功; false: 加锁失败
     */
    boolean lock(String key, long timeout);

    /**
     * 尝试加锁
     *
     * @param key key
     * @return true: 加锁成功; false: 加锁失败
     */
    boolean tryLock(String key);

    /**
     * 解锁
     *
     * @param key key
     */
    void unlock(String key);
}
