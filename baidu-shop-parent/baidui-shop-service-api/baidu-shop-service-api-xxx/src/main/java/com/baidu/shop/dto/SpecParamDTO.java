package com.baidu.shop.dto;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @program: baidu-shop-parent
 * @description: 规格的参数
 * @author: Mr.Zheng
 * @create: 2020-09-03 17:39
 **/
@Data
@ApiModel(value = "传输数据参数dto")
public class SpecParamDTO {


    @ApiModelProperty(value = "主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "分类id",example = "1")
    private Integer cid;

    @ApiModelProperty(value = "规格组id",example = "1")
    private Integer groupId;

    @ApiModelProperty(value = "参数名称")
    @NotEmpty(message = "参数名称不能为空",groups = {MingruiOperation.Add.class})
    private String name;

    @ApiModelProperty(value = "是否是数字类型参数,0为false-1为true",example = "0")
    @NotNull(message = "是否是数字参数不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Boolean numeric;

    @ApiModelProperty(value = "数字类型参数的单位,妃数字类型可以为空")
    private String unit;

    @ApiModelProperty(value = "是否是sku通用属性过滤,0为false-1为true",example = "0")
    @NotNull(message = "是否是数字参数不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Boolean generic;

    @ApiModelProperty(value = "是否用于搜索过滤,0为false-1为true",example = "0")
    @NotNull(message = "是否用于搜索过滤不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Boolean searching;

    @ApiModelProperty(value = "数值类型参数,如果需要搜索,则添加分段间隔值")
    private String segments;
}
