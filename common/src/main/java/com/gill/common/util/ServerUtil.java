package com.gill.common.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

/**
 * ServerUtil
 *
 * @author gill
 * @version 2024/01/31
 **/
public class ServerUtil {

    /**
     * 获取服务器id
     *
     * @param groupId id
     * @return id
     */
    public static String getRandomServerId(String groupId) {
        String ts = System.currentTimeMillis() + "";
        String randomStr = RandomUtil.randomString(8);
        return groupId + ":" + ts + ":" + randomStr;
    }

    /**
     * 获取cpu核数
     *
     * @return cpu核数
     */
    public static int getCpuCores() {
        String cpuLimit = System.getenv("CPU_LIMIT");
        if (StrUtil.isBlank(cpuLimit)) {
            return Runtime.getRuntime().availableProcessors();
        }
        return Integer.parseInt(cpuLimit);
    }
}
