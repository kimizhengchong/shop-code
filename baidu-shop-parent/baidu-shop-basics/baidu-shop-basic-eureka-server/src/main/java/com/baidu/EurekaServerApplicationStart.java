package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @program: baidu-shop-parent
 * @description: eurekaServer启动类
 * @author: Mr.Zheng
 * @create: 2020-08-27 14:28
 **/
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplicationStart {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplicationStart.class);
    }
}
