package com.baidu.shop.mapper;

import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.service.BrandService;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @program: baidu-shop-parent
 * @description:
 * @author: Mr.Zheng
 * @create: 2020-08-31 15:00
 **/
public interface BrandMapper extends Mapper<BrandEntity>, SelectByIdListMapper<BrandEntity,Integer> {

    @Select(value = "SELECT * FROM tb_brand b,tb_category_brand cb WHERE b.id = cb.brand_id AND cb.category_id=#{cid}")
    List<BrandEntity> getBrandByCategory(Integer cid);

}
