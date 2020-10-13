package com.baidu.shop.service.impl;

import com.baidu.shop.fiegn.BrandFeign;
import com.baidu.shop.fiegn.CategoryFeign;
import com.baidu.shop.fiegn.GoodsFeign;
import com.baidu.shop.fiegn.SpecificationFeign;
import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.TemplateStaticService;
import com.baidu.shop.utils.BaiduUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName TemplateImpl
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-09-25 19:13
 * @Version V1.0
 **/
@RestController
public class TemplateImpl extends BaseApiService implements TemplateStaticService {


    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    //注入静态化模版
    @Autowired
    private TemplateEngine templateEngine;

    //静态文件生成的路径
    @Value(value = "${mrshop.static.html.path}")
    private String staticHTMLPath;

    @Override
    public Result<JSONObject> createStaticHTMLTemplate(Integer spuId) {
        Map<String, Object> map = this.getPageInfoBySpuId(spuId);
        //创建模板引擎上下文
        Context context = new Context();
        //将所有准备的数据放到模板中
        context.setVariables(map);

        //创建文件 param1:文件路径 param2:文件名称
        File file = new File(staticHTMLPath, spuId + ".html");
        //构建文件输出流
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(file, "UTF-8");
            //根据模板生成静态文件
            //param1:模板名称 params2:模板上下文[上下文中包含了需要填充的数据],文件输出流
            templateEngine.process("item",context,writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> initStaticHTMLTemplate() {

        //获取所有的spu信息,注意:应该写一个只获取id集合的接口,我只是为了省事
        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(new SpuDTO());
        if(spuInfo.getCode() == 200){

            List<SpuDTO> spuList = spuInfo.getData();

            spuList.stream().forEach(spu -> {
                createStaticHTMLTemplate(spu.getId());
            });
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> delHTMLBySpuId(Integer spuId) {
        File file = new File(staticHTMLPath + File.separator + spuId + ".html");

        if(!file.delete()){
            return this.setResultError("文件删除失败");
        }
        return this.setResultSuccess();
    }

    //总方法
    private Map<String, Object> getPageInfoBySpuId(Integer spuId) {

        HashMap<String, Object> map = new HashMap<>();
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        Result<List<SpuDTO>> spuInfoResult = goodsFeign.getSpuInfo(spuDTO);

        //spu查询
        if (spuInfoResult.getCode() == 200) {
            if (spuInfoResult.getData().size() == 1){
                SpuDTO spuInfo = spuInfoResult.getData().get(0);
                map.put("spuInfo",spuInfo);

                //品牌查询
                List<BrandEntity> brandEntityList = this.getBrandEntity(spuInfo.getBrandId());
                map.put("brandInfo",brandEntityList.get(0));

                //分类查询
                Result<List<CategoryEntity>> categoryByIdsResult = this.getCategory(spuInfo);
                map.put("cateList",categoryByIdsResult.getData());

                //sku查询
                List<SkuDTO> skuResultData = this.getSkuResultData(spuId);
                map.put("skus",skuResultData);

                //spudetail信息
                SpuDetailEntity spuDetailResultData = this.getSpuDetail(spuId);
                map.put("spuDetailList",spuDetailResultData);

                //特有规格参数
                Map<Integer, String> specMap = this.getSpecMap(spuInfo.getCid3());
                map.put("specParamMap",specMap);

                //分组
                List<SpecGroupDTO> groupParams = this.getGroupParams(spuInfo.getCid3());
                map.put("groupParams",groupParams);
            }
        }
        return map;
    }

    //品牌查询
    private List<BrandEntity> getBrandEntity(Integer brandId){
        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setId(brandId);

        Result<PageInfo<BrandEntity>> brandInfoResult = brandFeign.getBrandInfo(brandDTO);
        if (brandInfoResult.getCode() == 200){
            PageInfo<BrandEntity> data = brandInfoResult.getData();
            List<BrandEntity> brandEntityList = data.getList();
            if (brandEntityList.size() == 1){
                return brandEntityList;
            }
        }
        return null;
    }
    //分类查询
    private Result<List<CategoryEntity>> getCategory(SpuDTO spuInfo){
        Result<List<CategoryEntity>> categoryByIdsResult = categoryFeign.getCategoryByIds(
                String.join(",", Arrays.asList(
                        spuInfo.getCid1()+"",
                        spuInfo.getCid2()+"",
                        spuInfo.getCid3()+""
                ))
        );
        if (categoryByIdsResult.getCode() == 200){
            return categoryByIdsResult;
        }
        return null;
    }
    //sku查询
    private List<SkuDTO> getSkuResultData(Integer spuId){
        Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spuId);
        if (skuResult.getCode() == 200){
            List<SkuDTO> skuResultData = skuResult.getData();
            return skuResultData;
        }
        return null;
    }
    //spudetail信息
    private SpuDetailEntity getSpuDetail(Integer spuId){
        Result<SpuDetailEntity> spuDetailResult = goodsFeign.getDetailBySpuId(spuId);
        if (spuDetailResult.getCode() == 200) {
            SpuDetailEntity spuDetailResultData = spuDetailResult.getData();
            return spuDetailResultData;
        }
        return null;
    }
    //特有规格参数
    private Map<Integer, String> getSpecMap(Integer cid){
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(cid);
        specParamDTO.setGeneric(false);
        Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamList(specParamDTO);
        if (specParamResult.getCode() == 200) {
            Map<Integer, String> specMap = new HashMap<>();
            specParamResult.getData().stream().forEach(spec -> {
                specMap.put(spec.getId(),spec.getName());
            });
            return specMap;
        }
        return null;
    }
    //分组
    private List<SpecGroupDTO> getGroupParams(Integer cid){
        SpecGroupDTO specGroupDTO = new SpecGroupDTO();
        specGroupDTO.setCid(cid);
        Result<List<SpecGroupEntity>> specGroupResult = specificationFeign.getSpecGroup(specGroupDTO);
        if (specGroupResult.getCode() == 200) {
            List<SpecGroupEntity> specGroupInfo = specGroupResult.getData();
            //规格组和规格参数
            List<SpecGroupDTO> groupParams = specGroupInfo.stream().map(specGroup -> {
                SpecGroupDTO dto = BaiduUtil.copyProperties(specGroup, SpecGroupDTO.class);
                //规格参数
                SpecParamDTO specGroupParamDTO = new SpecParamDTO();
                specGroupParamDTO.setGroupId(specGroup.getId());
                specGroupParamDTO.setGeneric(true);
                Result<List<SpecParamEntity>> specParamResultList = specificationFeign.getSpecParamList(specGroupParamDTO);
                if (specParamResultList.getCode() == 200) {
                    dto.setSpecParams(specParamResultList.getData());
                }
                return dto;
            }).collect(Collectors.toList());
            return groupParams;
        }
        return null;
    }
}
