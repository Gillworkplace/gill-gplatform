package com.gill.common.func;

/**
 * ThrowingConsumer
 *
 * @author zhangzhiyan
 * @date 2024/07/15
 */
public interface BiThrowingConsumer<T, U> {

    void accept(T t, U u) throws Throwable;
}
