package com.gill.dubbo.exception.handler;

import java.util.List;
import org.apache.dubbo.rpc.Result;

/**
 * ExceptionHandlers
 *
 * @author gill
 * @version 2024/02/24
 **/
public class ExceptionHandlers {

    private static final List<ExceptionHandler> EXCEPTION_HANDLERS = List.of(
        ServiceExceptionHandler.INSTANCE);

    /**
     * 处理异常
     *
     * @param result 结果
     */
    public static void resolveExceptionResult(Result result) {
        for (ExceptionHandler handler : EXCEPTION_HANDLERS) {
            if (handler.supportResolveResult(result)) {
                handler.resolveExceptionResult(result);
                return;
            }
        }
    }
}
