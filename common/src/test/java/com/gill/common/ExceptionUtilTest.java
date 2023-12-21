package com.gill.common;

import com.gill.common.exception.ExceptionUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * ExceptionUtilTest
 *
 * @author gill
 * @version 2023/12/21
 **/
public class ExceptionUtilTest {

    @Test
    public void testPrintAllMessage_WithNullThrowable() {
        // Arrange
        Throwable throwable = null;

        // Act
        String result = ExceptionUtil.getAllMessage(throwable);

        // Assert
        Assertions.assertEquals("", result);
    }

    @Test
    public void testPrintAllMessage_WithNoCause() {
        // Arrange
        Throwable throwable = new RuntimeException("Error message");

        // Act
        String result = ExceptionUtil.getAllMessage(throwable);

        // Assert
        Assertions.assertEquals("java.lang.RuntimeException: Error message", result);
    }

    @Test
    public void testPrintAllMessage_WithCause() {
        // Arrange
        Throwable throwable = new RuntimeException("Error message",
            new NullPointerException("Null pointer"));

        // Act
        String result = ExceptionUtil.getAllMessage(throwable);

        // Assert
        Assertions.assertEquals(
            "java.lang.RuntimeException: Error message ==> java.lang.NullPointerException: Null pointer",
            result);
    }

}
