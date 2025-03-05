package com.gill.redis.core;

import cn.hutool.core.collection.CollectionUtil;
import com.gill.common.api.DLock;
import com.gill.common.exception.ExceptionUtil;
import com.gill.common.func.ThrowingConsumer;
import com.gill.common.func.ThrowingRunnable;
import com.gill.common.func.ThrowingSupplier;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
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

    private final String projectName;

    public RedissonDLockAdapter(RedissonClient client, String projectName) {
        this.client = client;
        this.projectName = projectName;
    }

    public RLock getLock(String key) {
        return client.getLock(projectName + ":lock:" + key);
    }

    private RReadWriteLock getReadWriteLock(String key) {
        return client.getReadWriteLock(projectName + ":read-write-lock:" + key);
    }

    public RLock getReadLock(String key) {
        return getReadWriteLock(key).readLock();
    }

    public RLock getWriteLock(String key) {
        return getReadWriteLock(key).writeLock();
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
        RLock lock = getLock(key);
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
        RLock lock = getLock(key);
        try {
            lock.unlock();
        } catch (Exception e) {
            log.error("unlock {} error, e: {}", key, ExceptionUtil.getAllMessage(e));
        }
    }

    @Override
    public void tryLock(String key, ThrowingRunnable runnable, ThrowingRunnable failRunnable) {
        tryLock(List.of(key), runnable, k -> failRunnable.run());
    }

    @Override
    public <T> T tryLock(String key, ThrowingSupplier<T> runnable, ThrowingRunnable failRunnable) {
        return tryLock(List.of(key), runnable, k -> failRunnable.run());
    }

    @Override
    public void tryLock(List<String> keys, ThrowingRunnable runnable,
        ThrowingConsumer<String> failRunnable) {
        tryLock(keys, () -> {
            runnable.run();
            return null;
        }, failRunnable);
    }

    @Override
    public <T> T tryLock(List<String> keys, ThrowingSupplier<T> runnable,
        ThrowingConsumer<String> failRunnable) {
        return lock(keys, 0, runnable, failRunnable);
    }

    @Override
    public void lock(String key, ThrowingRunnable runnable, ThrowingRunnable failRunnable) {
        lock(key, LOCK_EXPIRED, runnable, failRunnable);
    }

    @Override
    public void lock(String key, long waitTimeSeconds, ThrowingRunnable runnable,
        ThrowingRunnable failRunnable) {
        lock(key, waitTimeSeconds, () -> {
            runnable.run();
            return null;
        }, failRunnable);
    }

    @Override
    public <T> T lock(String key, ThrowingSupplier<T> runnable, ThrowingRunnable failRunnable) {
        return lock(key, LOCK_EXPIRED, runnable, failRunnable);
    }

    @Override
    public <T> T lock(String key, long waitTimeSeconds, ThrowingSupplier<T> runnable,
        ThrowingRunnable failRunnable) {
        return lock(List.of(key), waitTimeSeconds, runnable, k -> failRunnable.run());
    }

    @Override
    public void lock(List<String> keys, ThrowingRunnable runnable,
        ThrowingConsumer<String> failRunnable) {
        lock(keys, LOCK_EXPIRED, runnable, failRunnable);
    }

    @Override
    public void lock(List<String> keys, long waitTimeSeconds, ThrowingRunnable runnable,
        ThrowingConsumer<String> failRunnable) {
        lock(keys, waitTimeSeconds, () -> {
            runnable.run();
            return null;
        }, failRunnable);
    }

    @Override
    public <T> T lock(List<String> keys, ThrowingSupplier<T> runnable,
        ThrowingConsumer<String> failRunnable) {
        return lock(keys, LOCK_EXPIRED, runnable, failRunnable);
    }

    @Override
    public <T> T lock(List<String> keys, long waitTimeSeconds, ThrowingSupplier<T> runnable,
        ThrowingConsumer<String> failRunnable) {
        List<RLock> locks = keys.stream().sorted().map(this::getLock).collect(Collectors.toList());
        try {
            for (RLock lock : locks) {

                // 加锁失败直接执行降级处理 并跳出
                if (!lock.tryLock(waitTimeSeconds, 60, TimeUnit.SECONDS)) {
                    failRunnable.accept(lock.getName());
                    return null;
                }
            }
            return runnable.get();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            log.error("when locking keys {}, occur exception ex: {}", keys,
                ExceptionUtil.getAllMessage(ex));
            throw new RuntimeException(ex);
        } finally {
            CollectionUtil.reverse(locks).forEach(lock -> {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            });
        }
    }

    public RLock tryLockAsync(String key, long batchId, Exception failLockException) {
        return lockAsync(key, 0, batchId, failLockException);
    }

    public RLock lockAsync(String key, long waitTimeSeconds, long batchId,
        Exception failLockException) {
        RLock lock = getLock(key);
        try {
            if (!lock.tryLockAsync(waitTimeSeconds, 60, TimeUnit.SECONDS, batchId).get()) {
                throw failLockException;
            }
            return lock;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("when locking key {}, occur exception ex: {}", key,
                ExceptionUtil.getAllMessage(ex));
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void tryReadLock(String key, ThrowingRunnable runnable, ThrowingRunnable failRunnable) {
        tryReadLock(List.of(key), runnable, k -> failRunnable.run());
    }

    @Override
    public <T> T tryReadLock(String key, ThrowingSupplier<T> runnable,
        ThrowingRunnable failRunnable) {
        return tryReadLock(List.of(key), runnable, k -> failRunnable.run());
    }

    @Override
    public void tryReadLock(List<String> keys, ThrowingRunnable runnable,
        ThrowingConsumer<String> failRunnable) {
        tryReadLock(keys, () -> {
            runnable.run();
            return null;
        }, failRunnable);
    }

    @Override
    public <T> T tryReadLock(List<String> keys, ThrowingSupplier<T> runnable,
        ThrowingConsumer<String> failRunnable) {
        return readLock(keys, 0, runnable, failRunnable);
    }

    @Override
    public void tryWriteLock(String key, ThrowingRunnable runnable, ThrowingRunnable failRunnable) {
        tryWriteLock(List.of(key), runnable, k -> failRunnable.run());
    }

    @Override
    public <T> T tryWriteLock(String key, ThrowingSupplier<T> runnable,
        ThrowingRunnable failRunnable) {
        return tryWriteLock(List.of(key), runnable, k -> failRunnable.run());
    }

    @Override
    public void tryWriteLock(List<String> keys, ThrowingRunnable runnable,
        ThrowingConsumer<String> failRunnable) {
        tryWriteLock(keys, () -> {
            runnable.run();
            return null;
        }, failRunnable);
    }

    @Override
    public <T> T tryWriteLock(List<String> keys, ThrowingSupplier<T> runnable,
        ThrowingConsumer<String> failRunnable) {
        return writeLock(keys, 0, runnable, failRunnable);
    }

    @Override
    public void readLock(String key, ThrowingRunnable runnable, ThrowingRunnable failRunnable) {
        readLock(key, LOCK_EXPIRED, runnable, failRunnable);
    }

    @Override
    public void readLock(String key, long waitTimeSeconds, ThrowingRunnable runnable,
        ThrowingRunnable failRunnable) {
        readLock(key, waitTimeSeconds, () -> {
            runnable.run();
            return null;
        }, failRunnable);
    }

    @Override
    public <T> T readLock(String key, ThrowingSupplier<T> runnable, ThrowingRunnable failRunnable) {
        return readLock(key, LOCK_EXPIRED, runnable, failRunnable);
    }

    @Override
    public <T> T readLock(String key, long waitTimeSeconds, ThrowingSupplier<T> runnable,
        ThrowingRunnable failRunnable) {
        return readLock(List.of(key), waitTimeSeconds, runnable, k -> failRunnable.run());
    }

    @Override
    public void readLock(List<String> keys, ThrowingRunnable runnable,
        ThrowingConsumer<String> failRunnable) {
        readLock(keys, LOCK_EXPIRED, runnable, failRunnable);
    }

    @Override
    public void readLock(List<String> keys, long waitTimeSeconds, ThrowingRunnable runnable,
        ThrowingConsumer<String> failRunnable) {
        readLock(keys, waitTimeSeconds, () -> {
            runnable.run();
            return null;
        }, failRunnable);
    }

    @Override
    public <T> T readLock(List<String> keys, ThrowingSupplier<T> runnable,
        ThrowingConsumer<String> failRunnable) {
        return readLock(keys, LOCK_EXPIRED, runnable, failRunnable);
    }

    @Override
    public <T> T readLock(List<String> keys, long waitTimeSeconds, ThrowingSupplier<T> runnable,
        ThrowingConsumer<String> failRunnable) {
        List<RLock> locks = keys.stream()
            .sorted()
            .map(this::getReadWriteLock)
            .map(RReadWriteLock::readLock)
            .collect(Collectors.toList());
        try {
            for (RLock lock : locks) {

                // 加锁失败直接执行降级处理 并跳出
                if (!lock.tryLock(waitTimeSeconds, 60, TimeUnit.SECONDS)) {
                    failRunnable.accept(lock.getName());
                    return null;
                }
            }
            return runnable.get();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            log.error("when locking keys {}, occur exception ex: {}", keys,
                ExceptionUtil.getAllMessage(ex));
            throw new RuntimeException();
        } finally {
            CollectionUtil.reverse(locks).forEach(lock -> {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            });
        }
    }

    @Override
    public void writeLock(String key, ThrowingRunnable runnable, ThrowingRunnable failRunnable) {
        writeLock(key, LOCK_EXPIRED, runnable, failRunnable);
    }

    @Override
    public void writeLock(String key, long waitTimeSeconds, ThrowingRunnable runnable,
        ThrowingRunnable failRunnable) {
        writeLock(key, waitTimeSeconds, () -> {
            runnable.run();
            return null;
        }, failRunnable);
    }

    @Override
    public <T> T writeLock(String key, ThrowingSupplier<T> runnable,
        ThrowingRunnable failRunnable) {
        return writeLock(key, LOCK_EXPIRED, runnable, failRunnable);
    }

    @Override
    public <T> T writeLock(String key, long waitTimeSeconds, ThrowingSupplier<T> runnable,
        ThrowingRunnable failRunnable) {
        return writeLock(List.of(key), waitTimeSeconds, runnable, k -> failRunnable.run());
    }

    @Override
    public void writeLock(List<String> keys, ThrowingRunnable runnable,
        ThrowingConsumer<String> failRunnable) {
        writeLock(keys, LOCK_EXPIRED, runnable, failRunnable);
    }

    @Override
    public void writeLock(List<String> keys, long waitTimeSeconds, ThrowingRunnable runnable,
        ThrowingConsumer<String> failRunnable) {
        writeLock(keys, waitTimeSeconds, () -> {
            runnable.run();
            return null;
        }, failRunnable);
    }

    @Override
    public <T> T writeLock(List<String> keys, ThrowingSupplier<T> runnable,
        ThrowingConsumer<String> failRunnable) {
        return writeLock(keys, LOCK_EXPIRED, runnable, failRunnable);
    }

    @Override
    public <T> T writeLock(List<String> keys, long waitTimeSeconds, ThrowingSupplier<T> runnable,
        ThrowingConsumer<String> failRunnable) {
        List<RLock> locks = keys.stream()
            .sorted()
            .map(this::getReadWriteLock)
            .map(RReadWriteLock::writeLock)
            .collect(Collectors.toList());
        try {
            for (RLock lock : locks) {

                // 加锁失败直接执行降级处理 并跳出
                if (!lock.tryLock(waitTimeSeconds, 60, TimeUnit.SECONDS)) {
                    failRunnable.accept(lock.getName());
                    return null;
                }
            }
            return runnable.get();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            log.error("when locking keys {}, occur exception ex: {}", keys,
                ExceptionUtil.getAllMessage(ex));
            throw new RuntimeException(ex);
        } finally {
            CollectionUtil.reverse(locks).forEach(lock -> {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            });
        }
    }

    public RLock tryReadLockAsync(String key, long batchId, RuntimeException failLockException) {
        return readLockAsync(key, 0, batchId, failLockException);
    }

    public RLock tryWriteLockAsync(String key, long batchId, RuntimeException failLockException) {
        return writeLockAsync(key, 0, batchId, failLockException);
    }

    public RLock readLockAsync(String key, long waitTimeSeconds, long batchId,
        RuntimeException failLockException) {
        RLock lock = getReadLock(key);
        try {
            if (!lock.tryLockAsync(waitTimeSeconds, 60, TimeUnit.SECONDS, batchId).get()) {
                throw failLockException;
            }
            return lock;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("when locking key {}, occur exception ex: {}", key,
                ExceptionUtil.getAllMessage(ex));
            throw new RuntimeException(ex);
        }
    }

    public RLock writeLockAsync(String key, long waitTimeSeconds, long batchId,
        RuntimeException failLockException) {
        RLock lock = getWriteLock(key);
        try {
            if (!lock.tryLockAsync(waitTimeSeconds, 60, TimeUnit.SECONDS, batchId).get()) {
                throw failLockException;
            }
            return lock;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("when locking key {}, occur exception ex: {}", key,
                ExceptionUtil.getAllMessage(ex));
            throw new RuntimeException(ex);
        }
    }
}
