package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.group.MingruiOperation;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品分类接口")
public interface CategoryService {

    @ApiOperation(value = "通过查询商品分类")
    @GetMapping(value = "category/list")
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid);

    @ApiOperation(value = "新增分类")
    @PostMapping(value = "category/add")
    public Result<JSONObject> addCategory(@Validated({MingruiOperation.Add.class}) @RequestBody CategoryEntity entity);

    @ApiOperation(value = "修改分类")
    @PutMapping(value = "category/edit")
    public Result<JSONObject> editCategory(@Validated({MingruiOperation.Update.class}) @RequestBody CategoryEntity entity);

    @ApiOperation(value = "通过id删除分类")
    @DeleteMapping(value = "category/del")
    public Result<JsonObject> delCategory(Integer id);

    @ApiOperation(value = "通过品牌id查询商品分类")
    @GetMapping(value = "category/getByBrand")
    public Result<List<CategoryEntity>> getByBrand(Integer brandId);

    @ApiOperation(value = "通过分类集合查询分类信息")
    @GetMapping(value = "category/getCategoryByIds")
    Result<List<CategoryEntity>> getCategoryByIds(@RequestParam String categoryIds);

}
