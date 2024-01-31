package com.gill.notification.worker.core.interceptor;

import com.gill.api.domain.AuthParamProperties;
import com.gill.api.domain.UserProperties;
import com.gill.api.user.UserService;
import com.gill.web.util.RequestUtil;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired(required = false)
    private UserService userService;

    @Override
    public boolean beforeHandshake(@Nonnull ServerHttpRequest request,
        @Nonnull ServerHttpResponse response, @Nonnull WebSocketHandler wsHandler,
        @Nonnull Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest httpRequest) {

            // 校验请求参数中的uid是否与token匹配
            HttpServletRequest servletRequest = httpRequest.getServletRequest();
            String uid = RequestUtil.resolveName(servletRequest,
                AuthParamProperties.USER_ID.getValue());
            String tid = RequestUtil.resolveName(servletRequest,
                AuthParamProperties.TOKEN_ID.getValue());
            if (userService == null) {
                return false;
            }
            attributes.put(UserProperties.USER_ID.getValue(), uid);
            return userService.checkToken(uid, tid);
        }
        return false;
    }

    @Override
    public void afterHandshake(@Nonnull ServerHttpRequest request,
        @Nonnull ServerHttpResponse response, @Nonnull WebSocketHandler wsHandler,
        Exception exception) {

    }
}
