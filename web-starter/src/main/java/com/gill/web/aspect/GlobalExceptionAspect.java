package com.gill.web.aspect;

import com.gill.common.exception.ExceptionUtil;
import com.gill.dubbo.exception.ServiceException;
import com.gill.web.api.Response;
import com.gill.web.exception.WebException;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

/**
 * GlobalExceptionAspect
 *
 * @author gill
 * @version 2024/01/22
 **/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionAspect {

    @ExceptionHandler(value = WebException.class)
    public Response<Object> handleWebEx(WebException ex) {
        if (ex.isInternalException()) {
            log.error("occur internal web exception: {}", ExceptionUtil.getAllMessage(ex));
            return Response.failed().build();
        }
        if (ex.isUnauthorized()) {
            return Response.unauthorized(ex.getMessage()).addHeader("WWW-Authenticate", "Basic realm=").build();
        }
        return Response.failed(ex.getStatus(), ex.getMessage()).build();
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class})
    public Response<Object> handleValidException(BindException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        if (!bindingResult.hasErrors()) {
            return Response.validateFailed().build();
        }
        String message = bindingResult.getFieldErrors().get(0).getDefaultMessage();
        return Response.validateFailed(message).build();
    }

    @ExceptionHandler(value = {HandlerMethodValidationException.class})
    public Response<Object> handleValidException2(HandlerMethodValidationException ex) {
        List<ParameterValidationResult> validateResults = ex.getAllValidationResults();
        StringBuilder error = new StringBuilder();
        for (ParameterValidationResult validate : validateResults) {
            for (MessageSourceResolvable resolvableError : validate.getResolvableErrors()) {
                error.append(resolvableError.getDefaultMessage()).append(";");
            }
        }
        return Response.validateFailed(error.toString()).build();
    }

    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public Response<Object> handleArgumentException(Exception ex) {
        return Response.validateFailed("参数错误").build();
    }

    @ExceptionHandler(value = {RpcException.class})
    public Response<Object> handleRpcException(RpcException ex) {
        log.error("occur rpc exception: {}", ExceptionUtil.getAllMessage(ex));

        // rpc exception -> execution exception -> service exception
        return Optional.of(ex).map(RpcException::getCause).map(Throwable::getCause).map(t -> {
            if (t instanceof ServiceException se) {
                return handleWebEx(new WebException(se));
            }
            return null;
        }).orElseGet(() -> Response.failed("通信错误").build());
    }

    @ExceptionHandler(value = Exception.class)
    public Response<Object> handleEx(Exception ex) {
        log.error("web occur exception, e: {}", ExceptionUtil.getAllMessage(ex));
        return Response.failed().build();
    }
}
