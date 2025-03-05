package com.gill.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * GillUserApplication
 *
 * @author gill
 */
@MapperScan("com.gill.**.mapper")
@SpringBootApplication
public class GillUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(GillUserApplication.class, args);
    }
}
