package com.gill.dubbo.filter;

import com.gill.common.exception.ExceptionUtil;
import com.gill.dubbo.exception.ServiceException;
import com.gill.dubbo.exception.handler.ExceptionHandlers;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

/**
 * ServiceFilter
 *
 * @author gill
 * @version 2024/02/22
 **/
@Slf4j
@Activate(group = CommonConstants.CONSUMER)
public class ServiceFilter implements Filter, Filter.Listener {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        return invoker.invoke(invocation);
    }


    @Override
    public void onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        if (result.hasException()) {
            ExceptionHandlers.resolveExceptionResult(result);
        }
    }

    @Override
    public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {
    }
}
