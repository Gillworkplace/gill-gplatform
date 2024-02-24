package com.gill.dubbo.exception.handler;

import com.gill.dubbo.exception.ServiceException;
import com.gill.dubbo.filter.ProviderExceptionFilter;
import org.apache.dubbo.rpc.Result;

/**
 * ServiceExceptionHandler
 *
 * @author gill
 * @version 2024/02/24
 **/
public class ServiceExceptionHandler implements ExceptionHandler {

    public static final ServiceExceptionHandler INSTANCE = new ServiceExceptionHandler();

    private ServiceExceptionHandler() {
    }

    /**
     * 是否支持处理该结果
     *
     * @param result 结果
     * @return 是否
     */
    @Override
    public boolean supportResolveResult(Result result) {
        return ServiceException.TYPE.equals(
            result.getAttachment(ProviderExceptionFilter.EXCEPTION_TYPE));
    }

    /**
     * 处理结果
     *
     * @param result 结果
     */
    @Override
    public void resolveExceptionResult(Result result) {
        int code = 500;
        try {
            code = Integer.parseInt(result.getAttachment(ServiceException.CODE));
        } catch (NumberFormatException ignored) {
        }
        String message = result.getAttachment(ServiceException.MESSAGE);
        throw new ServiceException(code, message);
    }
}
