package com.gill.dubbo.filter;

import com.gill.dubbo.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

/**
 * ProviderExceptionFilter
 *
 * @author gill
 * @version 2024/02/24
 **/
@Activate(group = CommonConstants.PROVIDER)
@Slf4j
public class ProviderExceptionFilter implements Filter, Filter.Listener {

    public static final String EXCEPTION_TYPE = "type";

    private static final String UNKNOWN_TYPE = "unknown_exception";

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        return invoker.invoke(invocation);
    }

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
        if (appResponse.hasException()) {
            if (appResponse.getException() instanceof ServiceException ex) {
                appResponse.setAttachment(EXCEPTION_TYPE, ServiceException.TYPE);
                appResponse.setAttachment(ServiceException.CODE, String.valueOf(ex.getCode()));
                appResponse.setAttachment(ServiceException.MESSAGE, ex.getMessage());
            } else {
                appResponse.setAttachment(EXCEPTION_TYPE, UNKNOWN_TYPE);
            }
        }
    }

    @Override
    public void onError(Throwable e, Invoker<?> invoker, Invocation invocation) {
    }
}
