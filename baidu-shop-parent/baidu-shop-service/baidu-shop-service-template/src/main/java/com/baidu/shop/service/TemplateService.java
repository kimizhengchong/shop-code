package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.Map;

public interface TemplateService {
    Map<String, Object> getPageInfoBySpuId(Integer spuId);



}
