package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName CarApplicationStart
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-10-19 19:38
 * @Version V1.0
 **/
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableEurekaClient
@EnableFeignClients
public class CarApplicationStart {

    public static void main(String[] args) {
        SpringApplication.run(CarApplicationStart.class);
    }

}
