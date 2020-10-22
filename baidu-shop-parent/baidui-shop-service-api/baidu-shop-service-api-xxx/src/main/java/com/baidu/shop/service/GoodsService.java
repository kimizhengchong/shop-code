package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品接口")
public interface GoodsService {

    @ApiOperation(value = "获取商品信息")
    @GetMapping(value = "goods/getSpuInfo")
    Result<List<SpuDTO>> getSpuInfo(@SpringQueryMap SpuDTO spuDTO);

    @ApiOperation(value = "新增商品信息")
    @PostMapping(value = "goods/add")
    Result<JSONObject> saveGoods(@RequestBody SpuDTO spuDTO);

    @ApiOperation(value = "通过spuid查询detail信息")
    @GetMapping(value = "good/getDetailBySpuId")
    Result<SpuDetailEntity> getDetailBySpuId(@RequestParam Integer spuId);

    @ApiOperation(value = "通过spuid查询sku信息")
    @GetMapping(value = "good/getSkuBySpuId")
    Result<List<SkuDTO>> getSkuBySpuId(@RequestParam Integer spuId);

    @ApiOperation(value = "修改商品信息")
    @PutMapping(value = "goods/add")
    Result<JSONObject> editGoods(@RequestBody SpuDTO spuDTO);

    @DeleteMapping(value = "goods/deleteGoods")
    @ApiOperation(value = "删除商品信息")
    Result<JSONObject> deleteGoods(Integer spuId);

    @ApiOperation(value = "通过skuId获取sku信息")
    @GetMapping(value = "goods/getSkuById")
    Result<SkuEntity> getSkuById(@RequestParam Long skuId);

//    @PutMapping(value = "goods/saleable")
//    @ApiOperation(value = "上架下架信息")
//    Result<JSONObject> saleableGoods(@RequestBody SpuDTO spuDTO);

    @GetMapping(value = "goods/saleable")
    @ApiOperation(value = "上架下架信息")
    Result<JSONObject> saleableGoods(@RequestParam Integer ids,@RequestParam Integer saleables);
}
