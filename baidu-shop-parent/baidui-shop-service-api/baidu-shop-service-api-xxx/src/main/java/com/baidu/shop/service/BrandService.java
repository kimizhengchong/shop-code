package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.group.MingruiOperation;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品接口")
public interface BrandService {

    @ApiOperation(value = "获取品牌信息")
    @GetMapping(value = "brand/getBrandInfo")
    Result<PageInfo<BrandEntity>> getBrandInfo(@SpringQueryMap BrandDTO brandDTO);


    @ApiOperation(value = "新增品牌信息")
    @PostMapping(value = "brand/save")
    Result<JsonObject> saveBrand(@Validated({MingruiOperation.Add.class}) @RequestBody BrandDTO brandDTO);

    @PutMapping(value = "brand/edit")
    @ApiOperation(value = "修改品牌信息")
    Result<JsonObject> editBrand(@Validated({MingruiOperation.Update.class}) @RequestBody BrandDTO brandDTO);


    @ApiOperation(value = "通过id删除品牌信息")
    @DeleteMapping(value = "brand/delete")
    Result<JsonObject> delete(Integer id);

    @GetMapping(value = "brand/getBrandByCategory")
    @ApiOperation(value = "通过分类信息查询品牌")
    Result<List<BrandEntity>> getBrandByCategory(Integer cid);

    @GetMapping(value = "brand/getBrandByIds")
    @ApiOperation(value = "通过id集合查询品牌")
    Result<List<BrandEntity>> getBrandByIds(@RequestParam String brandIds);

}
