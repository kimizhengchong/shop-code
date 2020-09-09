package com.baidu.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @program: baidu-shop-parent
 * @description:
 * @author: Mr.Zheng
 * @create: 2020-09-08 19:10
 **/
@ApiModel(value = "spu大字段传输数据")
@Data
public class SpuDetailDTO {

    @ApiModelProperty(value = "主键",example = "1")
    private Integer spuId;

    @ApiModelProperty(value = "商品描述信息")
    private String description;

    @ApiModelProperty(value = "通过规格参数数据")
    private String genericSpec;

    @ApiModelProperty(value = "特有规格参数及可选值信息,json格式")
    private String specialSpec;

    @ApiModelProperty(value = "包装清单")
    private String packingList;

    @ApiModelProperty(value = "售后服务")
    private String afterService;
}
