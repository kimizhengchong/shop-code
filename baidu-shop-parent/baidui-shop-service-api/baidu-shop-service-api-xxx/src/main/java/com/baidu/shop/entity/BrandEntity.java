package com.baidu.shop.entity;

import com.baidu.shop.group.MingruiOperation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @program: baidu-shop-parent
 * @description: 商品品牌实体类
 * @author: Mr.Zheng
 * @create: 2020-08-31 14:33
 **/
@Table(name = "tb_brand")
@Data
public class BrandEntity {

    @Id//主键
    @ApiModelProperty(value = "品牌主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {MingruiOperation.Update.class})
    @GeneratedValue(strategy = GenerationType.IDENTITY)//使用的主键生成策略
    private Integer id;

    @ApiModelProperty(value = "品牌名称")
    @NotEmpty(message = "名称不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private String name;

    @ApiModelProperty(value = "品牌图片")
    private String image;

    @ApiModelProperty(value = "品牌首字母")
    private Character letter;

}
