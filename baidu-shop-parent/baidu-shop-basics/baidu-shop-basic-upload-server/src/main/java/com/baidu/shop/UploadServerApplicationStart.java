package com.baidu.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @program: baidu-shop-parent
 * @description: 上传文件的启动类
 * @author: Mr.Zheng
 * @create: 2020-09-01 19:17
 **/
@SpringBootApplication
@EnableEurekaClient
public class UploadServerApplicationStart {

    public static void main(String[] args) {
        SpringApplication.run(UploadServerApplicationStart.class);
    }
}
