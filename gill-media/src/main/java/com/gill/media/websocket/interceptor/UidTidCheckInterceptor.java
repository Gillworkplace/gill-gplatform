package com.gill.media.websocket.interceptor;

import cn.hutool.core.util.StrUtil;
import com.gill.api.domain.UserProperties;
import com.gill.api.service.user.IUserService;
import com.gill.dubbo.contant.Filters;
import com.gill.web.util.RequestUtil;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;

/**
 * UidTidCheckInterceptor
 *
 * @author gill
 * @version 2024/01/31
 **/
@Component
@Order(1)
public class UidTidCheckInterceptor implements WebSocketInterceptor {

    @DubboReference(filter = Filters.CONSUMER, check = false, lazy = true)
    private IUserService userService;

    @Override
    public boolean beforeHandshake(@Nonnull ServerHttpRequest request,
        @Nonnull ServerHttpResponse response, @Nonnull WebSocketHandler wsHandler,
        @Nonnull Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest httpRequest) {

            // 校验请求参数中的uid是否与token匹配
            HttpServletRequest servletRequest = httpRequest.getServletRequest();
            String uid = RequestUtil.resolveName(servletRequest,
                UserProperties.USER_ID);
            String tid = RequestUtil.resolveName(servletRequest,
                UserProperties.TOKEN_ID);
            if (StrUtil.isEmpty(uid) || StrUtil.isEmpty(tid)) {
                return false;
            }
            attributes.put(UserProperties.USER_ID, uid);
            userService.checkToken(Integer.valueOf(uid), tid);
            return true;
        }
        return false;
    }
}
