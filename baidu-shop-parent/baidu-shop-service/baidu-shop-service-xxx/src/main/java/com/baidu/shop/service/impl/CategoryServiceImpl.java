package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.CategoryService;
import com.google.gson.JsonObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: baidu-shop-parent
 * @description:
 * @author: Mr.Zheng
 * @create: 2020-08-27 20:51
 **/
@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;


    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setParentId(pid);
        List<CategoryEntity> list = categoryMapper.select(categoryEntity);
        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<JSONObject> addCategory(CategoryEntity entity) {

        CategoryEntity parentCateEntity = new CategoryEntity();
        parentCateEntity.setId(entity.getParentId());
        parentCateEntity.setIsParent(1);
        categoryMapper.updateByPrimaryKeySelective(parentCateEntity);

            categoryMapper.insertSelective(entity);
        return this.setResultSuccess();
    }


    @Transactional
    @Override
    public Result<JSONObject> editCategory(CategoryEntity entity) {

        categoryMapper.updateByPrimaryKeySelective(entity);

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JsonObject> delCategory(Integer id) {
        //验证传入的id是否有效,并且查询出来的数据对接下来的程序有用
        CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(id);
        if (categoryEntity == null) {
            return this.setResultError("当前id不存在");
        }
        //判断当前节点是否为父节点
        if(categoryEntity.getIsParent() == 1){
            return this.setResultError("当前节点为父节点,不能删除");
        }

        //品牌信息传入分类信息
        Example exampleBrand = new Example(CategoryBrandEntity.class);
        //id传过来放进分类信息查询分类有没有被品牌绑定
        exampleBrand.createCriteria().andEqualTo("categoryId",id);
        //品牌查询分类信息
        List<CategoryBrandEntity> listBrand = categoryBrandMapper.selectByExample(exampleBrand);
        //判断分类有没有被品牌绑定
        if(listBrand.size() > 0){
            return this.setResultError("该分类已绑定品牌不能被删除");
        }

        //构建条件查询 通过组id查询当前分类
        Example exampleGroup = new Example(SpecGroupEntity.class);
        //把组id传过来对比
        exampleGroup.createCriteria().andEqualTo("cid",id);
        //list查询集合
        List<SpecGroupEntity> listGroup = specGroupMapper.selectByExample(exampleGroup);
        //判断分类下有没有组数据
        if(listGroup.size() > 0) {
            return this.setResultError("该分类已绑定规格分组不能被删除");
        }

        //构建条件查询 通过当前被删除节点的parentid查询数据
        Example example = new Example(CategoryEntity.class);
        example.createCriteria().andEqualTo("parentId",categoryEntity.getParentId());
        List<CategoryEntity> list = categoryMapper.selectByExample(example);
        //如果查询出来的数据只有一条
        if(list.size() == 1){//将父节点的isParent状态改为0
            CategoryEntity parentCateEntity = new CategoryEntity();
            parentCateEntity.setId(categoryEntity.getParentId());
            parentCateEntity.setIsParent(0);
            categoryMapper.updateByPrimaryKeySelective(parentCateEntity);
        }

        //如果分类下没有数据就执行删除
        categoryMapper.deleteByPrimaryKey(id);//执行删除
        return this.setResultSuccess();
    }


    @Override
    public Result<List<CategoryEntity>> getByBrand(Integer brandId) {

        List<CategoryEntity> byBrandId = categoryMapper.getByBrandId(brandId);

        return this.setResultSuccess(byBrandId);
    }

}
