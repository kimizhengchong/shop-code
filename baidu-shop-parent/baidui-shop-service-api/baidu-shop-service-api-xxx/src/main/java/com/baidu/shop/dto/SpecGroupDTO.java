package com.baidu.shop.dto;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @program: baidu-shop-parent
 * @description: 规格DTO
 * @author: Mr.Zheng
 * @create: 2020-09-03 11:50
 **/
@ApiModel(value = "规格传输数据DTO")
@Data
public class SpecGroupDTO {

    @ApiModelProperty(value = "主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "分类主键",example = "1")
    @NotNull(message = "类型id不能为空",groups = {MingruiOperation.Add.class})
    private Integer cid;

    @ApiModelProperty(value = "规格名称")
    @NotEmpty(message = "规格名称不能为空",groups = {MingruiOperation.Add.class})
    private String name;
}
