package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.entity.SpuEntity;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品接口")
public interface GoodsService {

    @ApiOperation(value = "获取商品信息")
    @GetMapping(value = "goods/getSpuInfo")
    Result<PageInfo<SpuEntity>> getSpuInfo(SpuDTO spuDTO);

    @ApiOperation(value = "新增商品信息")
    @PostMapping(value = "goods/add")
    Result<JSONObject> saveGoods(@RequestBody SpuDTO spuDTO);

    @ApiOperation(value = "通过spuid查询detail信息")
    @GetMapping(value = "good/getDetailBySpuId")
    Result<SpuDetailEntity> getDetailBySpuId(Integer spuId);

    @ApiOperation(value = "通过spuid查询sku信息")
    @GetMapping(value = "good/getSkuBySpuId")
    Result<List<SkuDTO>> getSkuBySpuId(Integer spuId);

    @ApiOperation(value = "修改商品信息")
    @PutMapping(value = "goods/add")
    Result<JSONObject> editGoods(@RequestBody SpuDTO spuDTO);

    @DeleteMapping(value = "goods/deleteGoods")
    @ApiOperation(value = "删除商品信息")
    Result<JSONObject> deleteGoods(Integer spuId);
}
