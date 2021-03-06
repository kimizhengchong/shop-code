package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Api(tags = "模板接口")
public interface TemplateStaticService {

    @ApiOperation(value = "创建静态HTML模板文件")
    @GetMapping(value = "template/createStaticHTMLTemplate")
    Result<JSONObject> createStaticHTMLTemplate(Integer spuId);

    @ApiOperation(value = "初始化静态HTML模板文件")
    @GetMapping(value = "template/initStaticHTMLTemplate")
    Result<JSONObject> initStaticHTMLTemplate();

    @ApiOperation(value = "删除静态HTML模板文件")
    @DeleteMapping(value = "template/delHTMLBySpuId")
    Result<JSONObject> delHTMLBySpuId(Integer spuId);

}
