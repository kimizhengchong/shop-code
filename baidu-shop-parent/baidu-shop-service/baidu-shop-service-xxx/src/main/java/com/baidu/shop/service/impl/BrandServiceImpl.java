package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.mapper.SkuMapper;
import com.baidu.shop.mapper.SpuMapper;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BaiduUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.PinyinUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Resource
    private SpuMapper spuMapper;


    @Override
    public Result<List<BrandEntity>> getBrandByIds(String brandIds) {
        List<Integer> collect = Arrays.asList(brandIds.split(","))
                .stream().map(idStr -> Integer.parseInt(idStr)).collect(Collectors.toList());
        List<BrandEntity> list = brandMapper.selectByIdList(collect);

        return this.setResultSuccess(list);
    }

    @Override
    public Result<List<BrandEntity>> getBrandByCategory(Integer cid) {
        List<BrandEntity> list = brandMapper.getBrandByCategory(cid);
        return this.setResultSuccess(list);
    }

    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO) {

        //判断分页不为空
        if(ObjectUtil.isNotNull(brandDTO.getPage()) && ObjectUtil.isNotNull(brandDTO.getRows())){
            PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());
        }

        //排序
        Example example = new Example(BrandEntity.class);
        Example.Criteria criteria = example.createCriteria();
        //条件查询
        if(ObjectUtil.isNotNull(brandDTO.getId()))
            criteria.andEqualTo("id",brandDTO.getId());
        if(StringUtil.isNotEmpty(brandDTO.getName()))
            criteria.andLike("name","%"+brandDTO.getName()+"%");

        if(!StringUtil.isEmpty(brandDTO.getOrder()))
            example.setOrderByClause(brandDTO.getOrderByClause());

        //查询
        List<BrandEntity> list = brandMapper.selectByExample(example);

        //数据封装
        PageInfo<BrandEntity> pageInfo = new PageInfo<BrandEntity>(list);

        //返回
        return this.setResultSuccess(pageInfo);
    }

    @Transactional
    @Override
    public Result<JsonObject> saveBrand(BrandDTO brandDTO) {

        //新增品牌并且返回主键
        BrandEntity brandEntity = BaiduUtil.copyProperties(brandDTO,BrandEntity.class);
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().charAt(0)),
                PinyinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));

        brandMapper.insertSelective(brandEntity);

        this.brandCategorySaveAndEidt(brandDTO,brandEntity);

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JsonObject> editBrand(BrandDTO brandDTO) {

        BrandEntity brandEntity = BaiduUtil.copyProperties(brandDTO,BrandEntity.class);
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().charAt(0)),
                PinyinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));

        //执行修改操作
        brandMapper.updateByPrimaryKeySelective(brandEntity);

        this.deleteBrandAndCategory(brandDTO.getId());

        //此处代码应该跟新增合并,将公共的代码抽取出来
       this.brandCategorySaveAndEidt(brandDTO,brandEntity);

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JsonObject> delete(Integer id) {

        Example example = new Example(SpuEntity.class);

        example.createCriteria().andEqualTo("brandId",id);

        List<SpuEntity> list = spuMapper.selectByExample(example);
        if(list.size() > 0) return this.setResultError("该品牌已被商品绑定不能被删除");

        //删除品牌
        brandMapper.deleteByPrimaryKey(id);

        //删除中间表数据
        this.deleteBrandAndCategory(id);

        return this.setResultSuccess("该品牌和中间表数据已被删除");
    }


    //删除中间表数据封装
    private void deleteBrandAndCategory(Integer id){
        //删除中间表数据
        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",id);
        categoryBrandMapper.deleteByExample(example);
    }

    //批量新增关系数据封装
    private void brandCategorySaveAndEidt(BrandDTO brandDTO,BrandEntity brandEntity){

        if(brandDTO.getCategory().contains(",")){
            List<CategoryBrandEntity> categoryBrandEntities = Arrays.asList(brandDTO.getCategory().split(","))
                    .stream().map(cid -> {

                        CategoryBrandEntity entity = new CategoryBrandEntity();
                        entity.setCategoryId(StringUtil.toInteger(cid));
                        entity.setBrandId(brandEntity.getId());

                        return entity;
                    }).collect(Collectors.toList());
            //批量新增
            categoryBrandMapper.insertList(categoryBrandEntities);
        }else{
            //新增
            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
            categoryBrandEntity.setCategoryId(StringUtil.toInteger(brandDTO.getCategory()));
            categoryBrandEntity.setBrandId(brandEntity.getId());

            categoryBrandMapper.insertSelective(categoryBrandEntity);
        }
    }
}
