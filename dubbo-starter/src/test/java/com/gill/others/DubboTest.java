package com.gill.others;

import com.gill.others.api.IDubboService;
import com.gill.others.api.IDubboServiceWithoutProvider;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * DubboTest
 *
 * @author gill
 * @version 2024/02/22
 **/
@SpringBootTest(classes = TestApplication.class, properties = "spring.config.location=classpath:application-test.yaml")
public class DubboTest extends BaseTest {

    @DubboReference(filter = "consumerFilter")
    private IDubboService dubboService;

    @DubboReference(filter = "consumerFilter", check = false, lazy = true)
    private IDubboServiceWithoutProvider dubboServiceWithoutProvider;

    @Test
    public void testDubbo() {
        Assertions.assertEquals("hello world", dubboService.hello("world"));
    }

    @Test
    public void testDubboWithoutProvider() {
        Assertions.assertThrows(RpcException.class,
            () -> dubboServiceWithoutProvider.hello("world"));
    }
}
