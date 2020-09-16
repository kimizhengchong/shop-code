package com.baidu.shop.dto;

import com.baidu.shop.base.BaseDTO;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @program: baidu-shop-parent
 * @description:
 * @author: Mr.Zheng
 * @create: 2020-09-07 14:22
 **/
@ApiModel(value = "spu传输数据DTO")
@Data
public class SpuDTO extends BaseDTO {

    @ApiModelProperty(value = "主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "标题")
    @NotEmpty(message = "标题不能为空",groups = {MingruiOperation.Add.class})
    private String title;

    @ApiModelProperty(value = "子标题")
    private String subTitle;

    @ApiModelProperty(value = "1级类目id",example = "1")
    @NotNull(message = "1级类目id不能为空",groups = {MingruiOperation.Add.class})
    private Integer cid1;

    @ApiModelProperty(value = "2级类目id",example = "1")
    @NotNull(message = "2级类目id不能为空",groups = {MingruiOperation.Add.class})
    private Integer cid2;

    @ApiModelProperty(value = "3级类目id",example = "1")
    @NotNull(message = "3级类目id不能为空",groups = {MingruiOperation.Add.class})
    private Integer cid3;

    @ApiModelProperty(value = "商品所属品牌id",example = "1")
    @NotNull(message = "品牌id不能为空",groups = {MingruiOperation.Add.class})
    private Integer brandId;

    @ApiModelProperty(value = "是否上架 1=true=上架,0=false=下架",example = "1")
    private Integer saleable;

    @ApiModelProperty(value = "是否有效 0=已删除,1=有效",example = "1")
    private Integer valid;

    @ApiModelProperty(value = "创建事件")
    private Date createTime;

    @ApiModelProperty(value = "最后修改时间")
    private Date lastUpdateTime;

    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    @ApiModelProperty(value = "分类名称")
    private String categoryName;

    @ApiModelProperty(value = "大字段数据")
    private SpuDetailDTO spuDetail;

    @ApiModelProperty(value = "sku属性集合")
    private List<SkuDTO> skus;
}

