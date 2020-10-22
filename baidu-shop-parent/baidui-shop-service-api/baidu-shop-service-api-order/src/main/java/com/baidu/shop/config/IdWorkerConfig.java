package com.baidu.shop.config;

import com.baidu.shop.utils.IdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName IdWorkerConfig
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-10-21 15:05
 * @Version V1.0
 **/
@Configuration
public class IdWorkerConfig {

    @Value(value = "${mrshop.worker.workerId}")
    private Long workerId;//当前工作机器Id

    @Value(value = "${mrshop.worker.datacenterId}")
    private Long datacenterId;//序列号Id

    @Bean
    public IdWorker idWorker(){
        return new IdWorker(workerId,datacenterId);
    }
}
