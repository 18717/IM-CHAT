package com.li2n.im_server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * 服务端启动类
 * @author 一杯香梨
 */

@ServletComponentScan
@SpringBootApplication
@MapperScan("com.li2n.im_server.mapper")
public class ImServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImServerApplication.class, args);
    }

}
