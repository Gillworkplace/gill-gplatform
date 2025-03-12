package com.gill.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * DateUtil
 *
 * @author zhangzhiyan
 * @since 2025-03-11
 */
public class DateUtil {

    /**
     * 转换成时间戳
     *
     * @param datetime 日期
     * @return 时间戳
     */
    public static long toTimestamp(LocalDateTime datetime) {
        return datetime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
