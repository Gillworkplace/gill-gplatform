package com.gill.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * OperationPermission
 *
 * @author gill
 * @version 2024/02/18
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface OperationPermission {

    /**
     * 权限表达式
     *
     * @return 表达式
     */
    String permissionExpression();

    /**
     * 异常码
     *
     * @return 异常码
     */
    int exceptionCode() default 400;

    /**
     * 异常消息
     *
     * @return 异常消息
     */
    String exceptionMessage() default "bad request";
}
