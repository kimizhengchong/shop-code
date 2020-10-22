package com.baidu.shop.fiegn;

import com.baidu.shop.service.GoodsService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName GoodsFeign
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-09-16 21:02
 * @Version V1.0
 **/
@FeignClient(contextId = "GoodsFeign",value = "xxx-service")
public interface GoodsFeign extends GoodsService {
}
