package com.gill.common.exception;

/**
 * ExceptionUtil
 *
 * @author gill
 * @version 2023/12/21
 **/
public class ExceptionUtil {


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
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage());
        while ((throwable = throwable.getCause()) != null) {
            sb.append(" ==> ").append(throwable.getClass().getName()).append(": ")
                .append(throwable.getMessage());
        }
        return sb.toString();
    }

}
