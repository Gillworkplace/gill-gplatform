package com.gill.common.exception;

/**
 * ExceptionUtil
 *
 * @author gill
 * @version 2023/12/21
 **/
public class ExceptionUtil {

    private static final int MAX_DEPTH = 10;

    /**
     * 打印异常的全部信息
     *
     * @param throwable throwable
     * @return 全部信息
     */
    public static String getAllMessage(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        String stackTrace = cn.hutool.core.exceptions.ExceptionUtil.stacktraceToString(throwable);
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage());
        int depth = 0;
        while ((throwable = throwable.getCause()) != null && depth <= MAX_DEPTH) {
            sb.append(" ==> ")
                .append(throwable.getClass().getName())
                .append(": ")
                .append(throwable.getMessage());
            depth++;
        }
        sb.append("\tstackTrace: ").append(stackTrace);
        return sb.toString();
    }
}
