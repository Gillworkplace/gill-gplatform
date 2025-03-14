package com.gill.dubbo.filter;

import com.gill.common.exception.BusinessCode;
import com.gill.common.exception.BusinessException;
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
                appResponse.setAttachment("type", ServiceException.EXCEPTION_TYPE);
                appResponse.setAttachment("code", String.valueOf(ex.getCode()));
                appResponse.setAttachment("message", ex.getMessage());
            } else if (appResponse.getException() instanceof BusinessException ex) {
                appResponse.setAttachment("type", BusinessException.EXCEPTION_TYPE);
                appResponse.setAttachment("code", String.valueOf(ex.getCode()));
                appResponse.setAttachment("message", ex.getMessage());
            } else {
                appResponse.setAttachment("type", BusinessException.EXCEPTION_TYPE);
                appResponse.setAttachment("code",
                    String.valueOf(BusinessCode.SYSTEM_ERROR.getCode()));
            }
        }
    }

    @Override
    public void onError(Throwable e, Invoker<?> invoker, Invocation invocation) {
    }
}
