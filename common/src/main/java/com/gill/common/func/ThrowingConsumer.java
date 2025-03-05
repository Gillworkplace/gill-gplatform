package com.gill.common.func;

/**
 * ThrowingConsumer
 *
 * @author zhangzhiyan
 * @date 2024/07/15
 */
public interface ThrowingConsumer<T> {

    void accept(T t) throws Throwable;
}
