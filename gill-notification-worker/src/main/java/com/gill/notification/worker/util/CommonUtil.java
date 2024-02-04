package com.gill.notification.worker.util;

import com.gill.api.domain.NotificationProperties;

/**
 * CommonUtil
 *
 * @author gill
 * @version 2024/02/04
 **/
public class CommonUtil {

    /**
     * 获取redis key
     *
     * @param id id
     * @return key
     */
    public static String getRedisKey(String id) {
        return NotificationProperties.REDIS_USER_LOCATION_PREFIX + id;
    }
}
