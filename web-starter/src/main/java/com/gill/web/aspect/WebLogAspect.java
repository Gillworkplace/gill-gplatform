package com.gill.web.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.gill.common.api.Pair;
import com.gill.common.exception.ExceptionUtil;
import com.gill.web.domain.WebLog;
import com.gill.web.util.RequestUtil;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * WebLogAspect
 *
 * @author gill
 * @version 2024/01/22
 **/
@Aspect
@Component
@Order(1)
@Slf4j
public class WebLogAspect {

    @Pointcut("execution(public * (@org.springframework.stereotype.Controller *).*(..))")
    public void controllerMethods() {
    }

    @Pointcut("execution(public * (@org.springframework.web.bind.annotation.RestController *).*(..))")
    public void restControllerMethods() {
    }

    @Pointcut("controllerMethods() || restControllerMethods()")
    public void webLog() {
    }

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object res = "";
        try {
            res = joinPoint.proceed();
            return res;
        } catch (Throwable ex) {
            res = ExceptionUtil.getAllMessage(ex);
            throw ex;
        } finally {
            WebLogBuilderContext context = new WebLogBuilderContext(joinPoint, startTime, res);
            WebLog webLog = WebLogBuilderChain.build(context);
            log.info("{}", JSONUtil.parse(webLog));
        }
//        Map<String, Object> logMap = new HashMap<>();
//        logMap.put("url", webLog.getUrl());
//        logMap.put("method", webLog.getMethod());
//        logMap.put("parameter", webLog.getParameter());
//        logMap.put("spendTime", webLog.getSpendTime());
//        logMap.put("description", webLog.getDescription());
////        LOGGER.info("{}", JSONUtil.parse(webLog));
//
//        LOGGER.info(Markers.appendEntries(logMap), JSONUtil.parse(webLog).toString());
//        return result;
    }

    @Getter
    static class WebLogBuilderContext {

        private final ProceedingJoinPoint joinPoint;

        private final Optional<HttpServletRequest> request;

        private final long startTime;

        private final Optional<Object> res;

        public WebLogBuilderContext(ProceedingJoinPoint joinPoint, long startTime, Object res) {
            this.joinPoint = joinPoint;
            this.startTime = startTime;
            this.res = Optional.ofNullable(res);
            this.request = getServletRequest();
        }

        private static Optional<HttpServletRequest> getServletRequest() {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return Optional.ofNullable(attributes).map(ServletRequestAttributes::getRequest);
        }
    }

    interface WebLogBuilder {

        /**
         * 构建weblog
         *
         * @param webLog  weblog对象
         * @param context 上下文
         */
        void build(WebLog webLog, WebLogBuilderContext context);
    }

    @Getter
    enum WebLogBuilderEnum {

        /**
         * 设置参数
         */
        PARAMETER(new ParamWebLogBuilder()),

        /**
         * 设置方向相关的属性
         */
        METHOD(new MethodWebLogBuilder()),

        /**
         * 设置请求相关的属性
         */
        REQUEST(new RequestWebLogBuilder()),

        /**
         * 设置通用属性
         */
        COMMON(new CommonWebLogBuilder());

        private final WebLogBuilder builder;

        WebLogBuilderEnum(WebLogBuilder webLogBuilder) {
            this.builder = webLogBuilder;
        }
    }

    static class WebLogBuilderChain {

        /**
         * 构建weblog
         *
         * @param context 上下文
         * @return weblog
         */
        public static WebLog build(WebLogBuilderContext context) {
            WebLog webLog = new WebLog();
            for (WebLogBuilderEnum builder : WebLogBuilderEnum.values()) {
                builder.getBuilder().build(webLog, context);
            }
            return webLog;
        }
    }

    static class ParamWebLogBuilder implements WebLogBuilder {

        @Getter
        enum ParamType {

            /**
             * 请求体
             */
            REQUEST_BODY(new RequestBodyConverter()),

            /**
             * 请求参数
             */
            REQUEST_PARAM(new RequestParamConverter()),

            /**
             * 路径参数
             */
            PATH_VARIABLE(new PathVariableConverter());

            private final ParamConverter converter;

            ParamType(ParamConverter converter) {
                this.converter = converter;
            }
        }

        static class ParamChain {

            /**
             * 转换参数
             *
             * @param parameter 参数
             * @param arg       参数实例
             * @return 转换后的参数
             */
            public static Object getParam(Parameter parameter, Object arg) {
                for (ParamType type : ParamType.values()) {
                    ParamConverter converter = type.getConverter();
                    if (converter.isSupport(parameter)) {
                        return converter.convert(parameter, arg);
                    }
                }
                return arg;
            }
        }

        interface ParamConverter {

            /**
             * 是否支持处理
             *
             * @param parameter 参数
             * @return 是否
             */
            boolean isSupport(Parameter parameter);

            /**
             * 获取参数
             *
             * @param parameter 参数
             * @param arg       参数
             * @return 参数
             */
            Object convert(Parameter parameter, Object arg);
        }

        static class RequestBodyConverter implements ParamConverter {

            /**
             * 是否支持处理
             *
             * @param parameter 参数
             * @return 是否
             */
            @Override
            public boolean isSupport(Parameter parameter) {
                return Objects.nonNull(parameter.getAnnotation(RequestBody.class));
            }

            /**
             * 获取参数
             *
             * @param parameter 参数
             * @param arg       参数
             * @return 参数
             */
            @Override
            public Object convert(Parameter parameter, Object arg) {
                return arg;
            }
        }

        static class RequestParamConverter implements ParamConverter {

            /**
             * 是否支持处理
             *
             * @param parameter 参数
             * @return 是否
             */
            @Override
            public boolean isSupport(Parameter parameter) {
                return Objects.nonNull(parameter.getAnnotation(RequestParam.class));
            }

            /**
             * 获取参数
             *
             * @param parameter 参数
             * @param arg       参数
             * @return 参数
             */
            @Override
            public Object convert(Parameter parameter, Object arg) {
                RequestParam anno = parameter.getAnnotation(RequestParam.class);
                String key = parameter.getName();
                if (!StrUtil.isEmpty(anno.value())) {
                    key = anno.value();
                }
                return Pair.of(key, arg);
            }
        }

        static class PathVariableConverter implements ParamConverter {

            /**
             * 是否支持处理
             *
             * @param parameter 参数
             * @return 是否
             */
            @Override
            public boolean isSupport(Parameter parameter) {
                return Objects.nonNull(parameter.getAnnotation(PathVariable.class));
            }

            /**
             * 获取参数
             *
             * @param parameter 参数
             * @param arg       参数
             * @return 参数
             */
            @Override
            public Object convert(Parameter parameter, Object arg) {
                PathVariable anno = parameter.getAnnotation(PathVariable.class);
                String key = parameter.getName();
                if (!StrUtil.isEmpty(anno.value())) {
                    key = anno.value();
                }
                return Pair.of(key, arg);
            }
        }

        @Override
        public void build(WebLog webLog, WebLogBuilderContext context) {
            Signature signature = context.getJoinPoint().getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            List<Object> argList = new ArrayList<>();
            Parameter[] parameters = method.getParameters();
            Object[] args = context.getJoinPoint().getArgs();
            for (int i = 0; i < parameters.length; i++) {
                Object arg = ParamChain.getParam(parameters[i], args[i]);
                argList.add(arg);
            }
            webLog.setParameter(argList);
        }
    }

    static class MethodWebLogBuilder implements WebLogBuilder {

        @Override
        public void build(WebLog webLog, WebLogBuilderContext context) {
            Signature signature = context.getJoinPoint().getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            if (method.isAnnotationPresent(ApiOperation.class)) {
                ApiOperation log = method.getAnnotation(ApiOperation.class);
                webLog.setDescription(log.value());
            }

        }
    }

    static class RequestWebLogBuilder implements WebLogBuilder {

        @Override
        public void build(WebLog webLog, WebLogBuilderContext context) {
            Optional<HttpServletRequest> request = context.getRequest();
            String url = request.map(HttpServletRequest::getRequestURL)
                .map(StringBuffer::toString)
                .orElse("");
            webLog.setUri(request.map(HttpServletRequest::getRequestURI).orElse(""));
            webLog.setUrl(url);
            webLog.setUsername(request.map(HttpServletRequest::getRemoteUser).orElse(""));
            webLog.setIp(RequestUtil.getRequestIp(request.orElse(null)));
            webLog.setBasePath(StrUtil.removeSuffix(url, URLUtil.url(url).getPath()));
        }
    }

    static class CommonWebLogBuilder implements WebLogBuilder {

        @Override
        public void build(WebLog webLog, WebLogBuilderContext context) {
            webLog.setStartTime(context.getStartTime());
            webLog.setCost(System.currentTimeMillis() - context.getStartTime());
            webLog.setResult(context.getRes().map(Object::toString).orElse(""));
        }
    }
}
