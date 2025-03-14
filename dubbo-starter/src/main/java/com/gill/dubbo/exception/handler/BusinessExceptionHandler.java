package com.gill.dubbo.exception.handler;

import com.gill.common.exception.BusinessCode;
import com.gill.common.exception.BusinessException;
import com.gill.dubbo.filter.ProviderExceptionFilter;
import org.apache.dubbo.rpc.Result;

/**
 * ServiceExceptionHandler
 *
 * @author gill
 * @version 2024/02/24
 **/
public class BusinessExceptionHandler implements ExceptionHandler {

    public static final BusinessExceptionHandler INSTANCE = new BusinessExceptionHandler();

    private BusinessExceptionHandler() {
    }

    /**
     * 是否支持处理该结果
     *
     * @param result 结果
     * @return 是否
     */
    @Override
    public boolean supportResolveResult(Result result) {
        return BusinessException.EXCEPTION_TYPE.equals(
            result.getAttachment(ProviderExceptionFilter.EXCEPTION_TYPE));
    }

    /**
     * 处理结果
     *
     * @param result 结果
     */
    @Override
    public void resolveExceptionResult(Result result) {
        BusinessCode code = BusinessCode.SYSTEM_ERROR;
        try {
            code = BusinessCode.getByCode(Integer.parseInt(result.getAttachment("code")));
        } catch (NumberFormatException ignored) {
        }
        String message = result.getAttachment("message");
        throw new BusinessException(code, message);
    }
}
