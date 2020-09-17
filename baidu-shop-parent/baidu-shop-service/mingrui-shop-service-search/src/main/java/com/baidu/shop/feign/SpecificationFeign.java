package com.baidu.shop.feign;

import com.baidu.shop.service.SpecGroupService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(contextId = "SpecGroupService",value = "xxx-service")
public interface SpecificationFeign extends SpecGroupService {
}
