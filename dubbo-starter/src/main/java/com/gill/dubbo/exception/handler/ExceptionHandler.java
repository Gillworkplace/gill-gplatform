package com.gill.dubbo.exception.handler;

import org.apache.dubbo.rpc.Result;

/**
 * ExceptionHandler
 *
 * @author gill
 * @version 2024/02/24
 **/
public interface ExceptionHandler {

    /**
     * 是否支持处理该结果
     *
     * @param result 结果
     * @return 是否
     */
    boolean supportResolveResult(Result result);

    /**
     * 处理结果
     *
     * @param result 结果
     */
    void resolveExceptionResult(Result result);
}
