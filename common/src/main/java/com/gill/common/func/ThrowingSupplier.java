package com.gill.common.func;

/**
 * ThrowingSupplier
 *
 * @author zhangzhiyan
 * @date 2024/07/15
 */
public interface ThrowingSupplier<T> {

    T get() throws Throwable;
}
