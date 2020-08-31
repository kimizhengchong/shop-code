package com.baidu.shop.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @program: baidu-shop-parent
 * @description:
 * @author: Mr.Zheng
 * @create: 2020-08-31 15:18
 **/
@Data
@ApiModel(value = "BaseDTO用于数据传输,其他dto需要继承此类")
public class BaseDTO{

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer page;

    @ApiModelProperty(value = "每页显示多少条",example = "5")
    private Integer rows;

    @ApiModelProperty(value = "排序字段")
    private String sort;

    @ApiModelProperty(value = "是否升序")
    private String order;


    @ApiModelProperty(hidden = true)
    public String getOrderByClause(){

        if(!StringUtils.isEmpty(sort))return sort + " " +
                order.replace("false","asc").replace("true","desc");
        return "";
    }
}
