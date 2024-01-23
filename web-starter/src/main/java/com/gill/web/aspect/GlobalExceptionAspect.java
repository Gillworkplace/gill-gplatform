package com.gill.web.aspect;

import com.gill.web.api.Result;
import com.gill.web.exception.WebException;
import java.util.List;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
public class GlobalExceptionAspect {

    @ExceptionHandler(value = WebException.class)
    public Result<String> handleWebEx(WebException ex) {
        if (ex.isInternalException()) {
            return Result.failed();
        }
        return Result.failed(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class})
    public Result<String> handleValidException(BindException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        if (!bindingResult.hasErrors()) {
            return Result.validateFailed();
        }
        StringBuilder error = new StringBuilder();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            error.append(fieldError.getField())
                .append(":")
                .append(fieldError.getDefaultMessage())
                .append("; ");
        }
        return Result.validateFailed(error.toString());
    }

    @ExceptionHandler(value = {HandlerMethodValidationException.class})
    public Result<String> handleValidException2(HandlerMethodValidationException ex) {
        List<ParameterValidationResult> validateResults = ex.getAllValidationResults();
        StringBuilder error = new StringBuilder();
        for (ParameterValidationResult validate : validateResults) {
            for (MessageSourceResolvable resolvableError : validate.getResolvableErrors()) {
                error.append(resolvableError.getDefaultMessage()).append(";");
            }
        }
        return Result.validateFailed(error.toString());
    }

    @ExceptionHandler(value = Exception.class)
    public Result<String> handleEx(Exception ex) {
        return Result.failed();
    }
}
