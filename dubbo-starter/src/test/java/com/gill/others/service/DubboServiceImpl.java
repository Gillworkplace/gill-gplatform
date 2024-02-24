package com.gill.others.service;

import com.gill.others.api.IDubboService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * DubboService
 *
 * @author gill
 * @version 2024/02/22
 **/
@DubboService
public class DubboServiceImpl implements IDubboService {

    @Override
    public String hello(String name) {
        return "hello " + name;
    }
}
