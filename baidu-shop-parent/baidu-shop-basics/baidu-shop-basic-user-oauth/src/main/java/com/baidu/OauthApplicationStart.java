package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName OauthApplicationStart
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-10-15 11:54
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
@MapperScan(value = "com.baidu.shop.mapper")
public class OauthApplicationStart {
    public static void main(String[] args) {
        SpringApplication.run(OauthApplicationStart.class);
    }
}
