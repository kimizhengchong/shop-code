package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName OrderStart
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-10-21 14:44
 * @Version V1.0
 **/
@SpringBootApplication
@MapperScan(value = "com.baidu.shop.mapper")
@EnableFeignClients
@EnableEurekaClient
public class OrderStart {

    public static void main(String[] args) {
        SpringApplication.run(OrderStart.class);
    }
}
