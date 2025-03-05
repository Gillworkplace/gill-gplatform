package com.gill.common.threadlocal;

/**
 * ThreadLocals
 *
 * @author zhangzhiyan
 * @since 2025-03-05
 */
public class ThreadLocals {

    /**
     * 用户ID
     */
    public static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    /**
     * token
     */
    public static final ThreadLocal<String> TOKEN = new ThreadLocal<>();

    /**
     * trace_id
     */
    public static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();
}
