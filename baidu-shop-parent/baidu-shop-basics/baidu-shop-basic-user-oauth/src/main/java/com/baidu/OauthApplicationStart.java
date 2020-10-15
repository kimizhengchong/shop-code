package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @ClassName OauthApplicationStart
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-10-15 11:54
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
public class OauthApplicationStart {
    public static void main(String[] args) {
        SpringApplication.run(OauthApplicationStart.class);
    }
}
