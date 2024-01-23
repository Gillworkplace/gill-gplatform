package com.gill.web.util;

import com.gill.common.exception.ExceptionUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;

/**
 * RequestUtil
 *
 * @author gill
 * @version 2024/01/23
 **/
@Slf4j
public class RequestUtil {

    private static final String UNKNOWN = "unknown";

    private static final String SPLIT = ",";

    private static final String LOCAL_HOST = "127.0.0.1";

    private static final String IPV6_HOST = "0:0:0:0:0:0:0:1";

    /**
     * 获取请求真实IP地址
     */
    public static String getRequestIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        //通过HTTP代理服务器转发时添加
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.isEmpty() || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();

            // 从本地访问时根据网卡取本机配置的IP
            if (LOCAL_HOST.equals(ipAddress) || IPV6_HOST.equals(ipAddress)) {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    ipAddress = inetAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    log.warn("unknown host, e: {}", ExceptionUtil.getAllMessage(e));
                }
            }
        }

        // 通过多个代理转发的情况，第一个IP为客户端真实IP，多个IP会按照','分割
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(SPLIT) > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }
}
