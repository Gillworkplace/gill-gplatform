package com.gill.common.func;

/**
 * ThrowingConsumer
 *
 * @author zhangzhiyan
 * @date 2024/07/15
 */
public interface ThrowingFunction<T, R> {

    R apply(T t) throws Throwable;
}
