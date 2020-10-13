package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.fiegn.BrandFeign;
import com.baidu.shop.fiegn.CategoryFeign;
import com.baidu.shop.fiegn.GoodsFeign;
import com.baidu.shop.fiegn.SpecificationFeign;
import com.baidu.shop.service.TemplateService;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.shop.utils.BaiduUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName TemplateServiceImpl
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-09-23 20:09
 * @Version V1.0
 **/
@Service
public class TemplateServiceImpl implements TemplateService {

    //@Autowired
    private GoodsFeign goodsFeign;

    //@Autowired
    private BrandFeign brandFeign;

    //@Autowired
    private CategoryFeign categoryFeign;

    //@Autowired
    private SpecificationFeign specificationFeign;



    @Override
    public Map<String, Object> getPageInfoBySpuId(Integer spuId) {

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
                BrandDTO brandDTO = new BrandDTO();
                brandDTO.setId(spuInfo.getBrandId());

                Result<PageInfo<BrandEntity>> brandInfoResult = brandFeign.getBrandInfo(brandDTO);
                if (brandInfoResult.getCode() == 200){
                    PageInfo<BrandEntity> data = brandInfoResult.getData();
                    List<BrandEntity> brandEntityList = data.getList();
                    if (brandEntityList.size() == 1){
                        map.put("brandInfo",brandEntityList.get(0));
                    }
                }
                //分类查询
                Result<List<CategoryEntity>> categoryByIdsResult = categoryFeign.getCategoryByIds(
                        String.join(",", Arrays.asList(
                                spuInfo.getCid1()+"",
                                spuInfo.getCid2()+"",
                                spuInfo.getCid3()+""
                        ))
                );
                if (categoryByIdsResult.getCode() == 200){
                    map.put("cateList",categoryByIdsResult.getData());
                }
                    //sku查询
                Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spuId);
                if (skuResult.getCode() == 200){
                    List<SkuDTO> skuResultData = skuResult.getData();
                    map.put("skus",skuResultData);
                }

                //spudetail信息
                Result<SpuDetailEntity> spuDetailResult = goodsFeign.getDetailBySpuId(spuId);
                if (spuDetailResult.getCode() == 200) {
                    SpuDetailEntity spuDetailResultData = spuDetailResult.getData();
                    map.put("spuDetailList",spuDetailResultData);
                }

                //特有规格参数
                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setCid(spuInfo.getCid3());
                specParamDTO.setGeneric(false);
                Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamList(specParamDTO);
                if (specParamResult.getCode() == 200) {
                    Map<Integer, String> specMap = new HashMap<>();
                    specParamResult.getData().stream().forEach(spec -> {
                        specMap.put(spec.getId(),spec.getName());
                        map.put("specParamMap",specMap);
                    });
                }

                //分组
                SpecGroupDTO specGroupDTO = new SpecGroupDTO();
                specGroupDTO.setCid(spuInfo.getCid3());
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
                    map.put("groupParams",groupParams);
                }
            }
        }
        return map;
    }
}
