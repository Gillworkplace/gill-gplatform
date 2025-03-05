package com.gill.web.interceptor;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.gill.common.threadlocal.ThreadLocals;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * TraceInterceptor
 *
 * @author zhangzhiyan
 * @since 2025-03-05
 */
@Slf4j
public class TraceInterceptor implements HandlerInterceptor {

    private static final String TRACE_ID = "trace-id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) {
        String traceId = request.getHeader(TRACE_ID);
        if (StrUtil.isBlank(traceId)) {
            traceId = "T" + IdUtil.getSnowflakeNextIdStr();
        }
        ThreadLocals.TRACE_ID.set(traceId);
        MDC.put(TRACE_ID, traceId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
        Object handler, Exception ex) throws Exception {
        ThreadLocals.TRACE_ID.remove();
        MDC.remove(TRACE_ID);
    }
}
