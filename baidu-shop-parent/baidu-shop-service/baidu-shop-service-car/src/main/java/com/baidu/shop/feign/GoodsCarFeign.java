package com.baidu.shop.feign;

import com.baidu.shop.service.GoodsService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName GoodsFeign
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-10-20 15:13
 * @Version V1.0
 **/
@FeignClient(contextId = "GoodsService", value = "xxx-service")
public interface GoodsCarFeign extends GoodsService {
}
