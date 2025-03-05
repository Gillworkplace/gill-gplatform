package com.gill.common.api;

import com.gill.common.func.ThrowingConsumer;
import com.gill.common.func.ThrowingRunnable;
import com.gill.common.func.ThrowingSupplier;
import java.util.List;

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

    /**
     * 快速失败 (获取锁的过期时间为1分钟)
     *
     * @param key          key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    void tryLock(String key, ThrowingRunnable runnable, ThrowingRunnable failRunnable);

    /**
     * 快速失败 (获取锁的过期时间为1分钟)
     *
     * @param key          key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    <T> T tryLock(String key, ThrowingSupplier<T> runnable, ThrowingRunnable failRunnable);

    /**
     * 快速失败 (获取锁的过期时间为1分钟)
     *
     * @param keys         key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    void tryLock(List<String> keys, ThrowingRunnable runnable,
        ThrowingConsumer<String> failRunnable);

    /**
     * 快速失败 (获取锁的过期时间为1分钟)
     *
     * @param keys         key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    <T> T tryLock(List<String> keys, ThrowingSupplier<T> runnable,
        ThrowingConsumer<String> failRunnable);

    /**
     * 阻塞加锁 (获取锁的超时时间为1分钟)
     *
     * @param key          key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    void lock(String key, ThrowingRunnable runnable, ThrowingRunnable failRunnable);

    /**
     * 阻塞加锁 (获取锁的超时时间为1分钟)
     *
     * @param key             key
     * @param waitTimeSeconds 阻塞时间 (秒)
     * @param runnable        执行方法
     * @param failRunnable    获取锁失败的处理逻辑
     */
    void lock(String key, long waitTimeSeconds, ThrowingRunnable runnable,
        ThrowingRunnable failRunnable);

    /**
     * 阻塞加锁 (获取锁的超时时间为1分钟)
     *
     * @param key          key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    <T> T lock(String key, ThrowingSupplier<T> runnable, ThrowingRunnable failRunnable);

    /**
     * 阻塞加锁 (获取锁的超时时间为1分钟)
     *
     * @param key             key
     * @param waitTimeSeconds 阻塞时间 (秒)
     * @param runnable        执行方法
     * @param failRunnable    获取锁失败的处理逻辑
     */
    <T> T lock(String key, long waitTimeSeconds, ThrowingSupplier<T> runnable,
        ThrowingRunnable failRunnable);

    /**
     * 阻塞加锁 (获取锁的超时时间为1分钟)
     *
     * @param keys         key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    void lock(List<String> keys, ThrowingRunnable runnable, ThrowingConsumer<String> failRunnable);

    /**
     * 阻塞加锁 (获取锁的超时时间为1分钟)
     *
     * @param keys            key
     * @param waitTimeSeconds 阻塞时间 (秒)
     * @param runnable        执行方法
     * @param failRunnable    获取锁失败的处理逻辑
     */
    void lock(List<String> keys, long waitTimeSeconds, ThrowingRunnable runnable,
        ThrowingConsumer<String> failRunnable);

    /**
     * 阻塞加锁 (获取锁的超时时间为1分钟)
     *
     * @param keys         key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    <T> T lock(List<String> keys, ThrowingSupplier<T> runnable,
        ThrowingConsumer<String> failRunnable);

    /**
     * 阻塞加锁 (获取锁的超时时间为1分钟)
     *
     * @param keys            key
     * @param waitTimeSeconds 阻塞时间 (秒)
     * @param runnable        执行方法
     * @param failRunnable    获取锁失败的处理逻辑
     */
    <T> T lock(List<String> keys, long waitTimeSeconds, ThrowingSupplier<T> runnable,
        ThrowingConsumer<String> failRunnable);

    /**
     * 快速失败 (获取读锁的过期时间为1分钟)
     *
     * @param key          key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    void tryReadLock(String key, ThrowingRunnable runnable, ThrowingRunnable failRunnable);

    /**
     * 快速失败 (获取读锁的过期时间为1分钟)
     *
     * @param key          key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    <T> T tryReadLock(String key, ThrowingSupplier<T> runnable, ThrowingRunnable failRunnable);

    /**
     * 快速失败 (获取读锁的过期时间为1分钟)
     *
     * @param keys         key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    void tryReadLock(List<String> keys, ThrowingRunnable runnable,
        ThrowingConsumer<String> failRunnable);

    /**
     * 快速失败 (获取读锁的过期时间为1分钟)
     *
     * @param keys         key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    <T> T tryReadLock(List<String> keys, ThrowingSupplier<T> runnable,
        ThrowingConsumer<String> failRunnable);

    /**
     * 快速失败 (获取写锁的过期时间为1分钟)
     *
     * @param key          key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    void tryWriteLock(String key, ThrowingRunnable runnable, ThrowingRunnable failRunnable);

    /**
     * 快速失败 (获取写锁的过期时间为1分钟)
     *
     * @param key          key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    <T> T tryWriteLock(String key, ThrowingSupplier<T> runnable, ThrowingRunnable failRunnable);

    /**
     * 快速失败 (获取写锁的过期时间为1分钟)
     *
     * @param keys         key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    void tryWriteLock(List<String> keys, ThrowingRunnable runnable,
        ThrowingConsumer<String> failRunnable);

    /**
     * 快速失败 (获取写锁的过期时间为1分钟)
     *
     * @param keys         key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    <T> T tryWriteLock(List<String> keys, ThrowingSupplier<T> runnable,
        ThrowingConsumer<String> failRunnable);

    /**
     * 阻塞加锁 (获取锁的超时时间为1分钟)
     *
     * @param key          key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    void readLock(String key, ThrowingRunnable runnable, ThrowingRunnable failRunnable);

    /**
     * 阻塞加锁 (获取锁的超时时间为1分钟)
     *
     * @param key             key
     * @param waitTimeSeconds 阻塞时间 (秒)
     * @param runnable        执行方法
     * @param failRunnable    获取锁失败的处理逻辑
     */
    void readLock(String key, long waitTimeSeconds, ThrowingRunnable runnable,
        ThrowingRunnable failRunnable);

    /**
     * 阻塞加锁 (获取锁的超时时间为1分钟)
     *
     * @param key          key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    <T> T readLock(String key, ThrowingSupplier<T> runnable, ThrowingRunnable failRunnable);

    /**
     * 阻塞加锁 (获取锁的超时时间为1分钟)
     *
     * @param key             key
     * @param waitTimeSeconds 阻塞时间 (秒)
     * @param runnable        执行方法
     * @param failRunnable    获取锁失败的处理逻辑
     */
    <T> T readLock(String key, long waitTimeSeconds, ThrowingSupplier<T> runnable,
        ThrowingRunnable failRunnable);

    /**
     * 阻塞加锁 (获取锁的超时时间为1分钟)
     *
     * @param keys         key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    void readLock(List<String> keys, ThrowingRunnable runnable,
        ThrowingConsumer<String> failRunnable);

    /**
     * 阻塞加锁 (获取锁的超时时间为1分钟)
     *
     * @param keys            key
     * @param waitTimeSeconds 阻塞时间 (秒)
     * @param runnable        执行方法
     * @param failRunnable    获取锁失败的处理逻辑
     */
    void readLock(List<String> keys, long waitTimeSeconds, ThrowingRunnable runnable,
        ThrowingConsumer<String> failRunnable);

    /**
     * 阻塞加锁 (获取锁的超时时间为1分钟)
     *
     * @param keys         key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    <T> T readLock(List<String> keys, ThrowingSupplier<T> runnable,
        ThrowingConsumer<String> failRunnable);

    /**
     * 阻塞加锁 (获取锁的超时时间为1分钟)
     *
     * @param keys            key
     * @param waitTimeSeconds 阻塞时间 (秒)
     * @param runnable        执行方法
     * @param failRunnable    获取锁失败的处理逻辑
     */
    <T> T readLock(List<String> keys, long waitTimeSeconds, ThrowingSupplier<T> runnable,
        ThrowingConsumer<String> failRunnable);

    /**
     * 阻塞加锁 (获取写锁的超时时间为1分钟)
     *
     * @param key          key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    void writeLock(String key, ThrowingRunnable runnable, ThrowingRunnable failRunnable);

    /**
     * 阻塞加锁 (获取写锁的超时时间为1分钟)
     *
     * @param key             key
     * @param waitTimeSeconds 阻塞时间 (秒)
     * @param runnable        执行方法
     * @param failRunnable    获取锁失败的处理逻辑
     */
    void writeLock(String key, long waitTimeSeconds, ThrowingRunnable runnable,
        ThrowingRunnable failRunnable);

    /**
     * 阻塞加锁 (获取写锁的超时时间为1分钟)
     *
     * @param key          key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    <T> T writeLock(String key, ThrowingSupplier<T> runnable, ThrowingRunnable failRunnable);

    /**
     * 阻塞加锁 (获取写锁的超时时间为1分钟)
     *
     * @param key             key
     * @param waitTimeSeconds 阻塞时间 (秒)
     * @param runnable        执行方法
     * @param failRunnable    获取锁失败的处理逻辑
     */
    <T> T writeLock(String key, long waitTimeSeconds, ThrowingSupplier<T> runnable,
        ThrowingRunnable failRunnable);

    /**
     * 阻塞加锁 (获取写锁的超时时间为1分钟)
     *
     * @param keys         key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    void writeLock(List<String> keys, ThrowingRunnable runnable,
        ThrowingConsumer<String> failRunnable);

    /**
     * 阻塞加锁 (获取写锁的超时时间为1分钟)
     *
     * @param keys            key
     * @param waitTimeSeconds 阻塞时间 (秒)
     * @param runnable        执行方法
     * @param failRunnable    获取锁失败的处理逻辑
     */
    void writeLock(List<String> keys, long waitTimeSeconds, ThrowingRunnable runnable,
        ThrowingConsumer<String> failRunnable);

    /**
     * 阻塞加锁 (获取锁的超时时间为1分钟)
     *
     * @param keys         key
     * @param runnable     执行方法
     * @param failRunnable 获取锁失败的处理逻辑
     */
    <T> T writeLock(List<String> keys, ThrowingSupplier<T> runnable,
        ThrowingConsumer<String> failRunnable);

    /**
     * 阻塞加锁 (获取锁的超时时间为1分钟)
     *
     * @param keys            key
     * @param waitTimeSeconds 阻塞时间 (秒)
     * @param runnable        执行方法
     * @param failRunnable    获取锁失败的处理逻辑
     */
    <T> T writeLock(List<String> keys, long waitTimeSeconds, ThrowingSupplier<T> runnable,
        ThrowingConsumer<String> failRunnable);
}
