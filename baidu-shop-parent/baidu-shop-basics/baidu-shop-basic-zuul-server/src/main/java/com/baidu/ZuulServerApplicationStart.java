package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @program: baidu-shop-parent
 * @description:
 * @author: Mr.Zheng
 * @create: 2020-08-28 14:03
 **/
@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
public class ZuulServerApplicationStart {

    public static void main(String[] args) {
        SpringApplication.run(ZuulServerApplicationStart.class);
    }
}
