package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "规格接口")
public interface SpecGroupService {

    @ApiOperation(value = "通过条件查询规格组")
    @GetMapping(value = "specgroup/list")
    Result<List<SpecGroupEntity>> getSpecGroup(SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "新增规格组")
    @PostMapping(value = "specgroup/add")
    Result<JSONObject> add(@Validated({MingruiOperation.Add.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "修改规格组")
    @PutMapping(value = "specgroup/add")
    Result<JSONObject> edit(@Validated({MingruiOperation.Update.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation(value = "删除规格组")
    @DeleteMapping(value = "specgroup/delete")
    Result<JSONObject> delete(Integer id);


    @ApiOperation(value = "参数查询")
    @GetMapping(value = "specparam/list")
    Result<List<SpecParamEntity>> getSpecParamList(@SpringQueryMap SpecParamDTO specParamDTO);

    @ApiOperation(value = "参数新增")
    @PostMapping(value = "specParam/add")
    Result<JSONObject> add(@Validated({MingruiOperation.Add.class}) @RequestBody SpecParamDTO specParamDTO);

    @ApiOperation(value = "参数新增")
    @PutMapping(value = "specParam/add")
    Result<JSONObject> edit(@Validated({MingruiOperation.Update.class}) @RequestBody SpecParamDTO specParamDTO);

    @ApiOperation(value = "删除规格参数")
    @DeleteMapping(value = "specParam/delete")
    Result<JSONObject> deleteParam(Integer id);
}
