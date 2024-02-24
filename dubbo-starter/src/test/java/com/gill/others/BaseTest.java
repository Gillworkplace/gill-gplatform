package com.gill.others;

import org.apache.curator.test.TestingServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * BaseTest
 *
 * @author gill
 * @version 2024/02/23
 **/
public class BaseTest {

    protected static TestingServer server;

    /**
     * 构造方法之后执行.
     */
    @BeforeAll
    public static void start() throws Exception {
        server = new TestingServer(22181);
        server.start();
    }

    @AfterAll
    public static void stop() throws Exception {
        server.stop();
    }
}
