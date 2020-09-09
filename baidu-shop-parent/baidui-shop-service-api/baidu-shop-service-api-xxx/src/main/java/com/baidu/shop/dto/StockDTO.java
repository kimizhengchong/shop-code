package com.baidu.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @program: baidu-shop-parent
 * @description:
 * @author: Mr.Zheng
 * @create: 2020-09-08 19:33
 **/
@ApiModel(value = "stock库存数据传输DTO")
@Data
public class StockDTO {

    @ApiModelProperty(value = "库存对应商品skuid",example = "1")
    private Long skuId;

    @ApiModelProperty(value = "可秒杀库存")
    private Integer seckillStock;

    @ApiModelProperty(value = "秒杀总数量")
    private Integer seckillTotal;

    @ApiModelProperty(value = "库存数量")
    private Integer stock;
}
