package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseDTO;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BaiduUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: baidu-shop-parent
 * @description: 商品实现类
 * @author: Mr.Zheng
 * @create: 2020-08-31 15:02
 **/
@RestController
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Resource
    private BrandMapper brandMapper;

    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO) {

        //分页
        PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());

        //排序
        Example example = new Example(BrandEntity.class);
        if(!StringUtils.isEmpty(brandDTO.getOrder()))example.setOrderByClause(brandDTO.getOrderByClause());


        //条件查询
        if (StringUtil.isNotEmpty(brandDTO.getName())) example.createCriteria()
                .andLike("name","%" + brandDTO.getName() + "%");
        //查询
        List<BrandEntity> list = brandMapper.selectByExample(example);

        //数据封装
        PageInfo<BrandEntity> pageInfo = new PageInfo<BrandEntity>(list);

        return this.setResultSuccess(pageInfo);
    }

    @Transactional
    @Override
    public Result<JsonObject> saveBrand(BrandDTO brandDTO) {

        brandMapper.insertSelective(BaiduUtil.copyProperties(brandDTO,BrandEntity.class));

        return this.setResultSuccess();
    }


}
