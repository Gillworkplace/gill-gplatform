package com.gill.common.func;

/**
 * ThrowingConsumer
 *
 * @author zhangzhiyan
 * @date 2024/07/15
 */
public interface BiThrowingFunction<T, U, R> {

    R apply(T t, U u) throws Throwable;
}
