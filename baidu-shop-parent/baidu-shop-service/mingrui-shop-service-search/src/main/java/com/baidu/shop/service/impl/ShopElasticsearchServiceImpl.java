package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.response.GoodsResponse;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.ShopElasticsearchService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.ESHighLightUtil;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ShopElasticsearchServiceImpl
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-09-16 21:03
 * @Version V1.0
 **/
@RestController
@Slf4j
public class ShopElasticsearchServiceImpl extends BaseApiService implements ShopElasticsearchService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;



    @Override
    public GoodsResponse search(String search,Integer page,String filter) {
        //判断搜索的内容不能为空
        if (StringUtil.isEmpty(search)) throw new RuntimeException("查询id不能为空");
        //构建条件查询
        SearchHits<GoodsDoc> hits = elasticsearchRestTemplate.search(this.getSearchQueryBuilder(search,page,filter).build(), GoodsDoc.class);
        List<SearchHit<GoodsDoc>> hit = ESHighLightUtil.getHighLightHit(hits.getSearchHits());
        //返回的商品集合
        List<GoodsDoc> goodsList = hit.stream().map(searchHit -> searchHit.getContent()).collect(Collectors.toList());
        //获得分页的总条数
        long total = hits.getTotalHits();
        //通过基本数据类型装箱拆箱获得总页数
        long totalPage = Double.valueOf(Math.ceil(Long.valueOf(total).doubleValue() / 10)).longValue();
        Aggregations aggregations = hits.getAggregations();
        //分类集合
        Map<Integer, List<CategoryEntity>> map = this.getCategoryList(aggregations);
        List<CategoryEntity> categoryList = null;
        Integer cidHot = 0;
        for (Map.Entry<Integer,List<CategoryEntity>> mapEntry : map.entrySet()){
            cidHot = mapEntry.getKey();
            categoryList = mapEntry.getValue();
        }
        //获取规格参数
        Map<String, List<String>> specParamValueMap = this.getSpecParam(cidHot, search);
        //品牌集合
        List<BrandEntity> brandList = this.getBrandList(aggregations);
        return new GoodsResponse(total, totalPage, brandList, categoryList, goodsList,specParamValueMap);
    }

    @Override
    public Result<JSONObject> saveData(Integer spuId) {
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        List<GoodsDoc> goodsDocs = this.esGoodsInfo(spuDTO);
        GoodsDoc goodsDoc = goodsDocs.get(0);
        elasticsearchRestTemplate.save(goodsDoc);

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> delData(Integer spuId) {
        GoodsDoc goodsDoc = new GoodsDoc();
        goodsDoc.setId(spuId.longValue());
        elasticsearchRestTemplate.delete(goodsDoc);
        return this.setResultSuccess();
    }


    //获取规格参数
    private Map<String, List<String>> getSpecParam(Integer cidHot,String search){
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(cidHot);
        specParamDTO.setSearching(true);
        Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamList(specParamDTO);
        if (specParamResult.getCode() == 200){
            List<SpecParamEntity> specParamList = specParamResult.getData();
            //聚合查询
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));
            //分页必须查询一条数据
            queryBuilder.withPageable(PageRequest.of(0,1));
            specParamList.stream().forEach(specParam ->{
                queryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs." + specParam.getName() +".keyword"));
            });
            SearchHits<GoodsDoc> search1 = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsDoc.class);

            Map<String, List<String>> map = new HashMap<>();
            Aggregations aggregations = search1.getAggregations();
            specParamList.stream().forEach(specParam ->{
                Terms terms = aggregations.get(specParam.getName());
                List<? extends Terms.Bucket> buckets = terms.getBuckets();
                List<String> collect = buckets.stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
                map.put(specParam.getName(),collect);
            });
            return map;
        }
        return null;
    }


    /**
     * 构建条件查询
     * @param search
     * @param page
     * @return
     */
    private NativeSearchQueryBuilder getSearchQueryBuilder(String search,Integer page,String filter){
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();

        if (StringUtil.isNotEmpty(filter) && filter.length() > 2){
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            Map<String, String> stringFilterMap = JSONUtil.toMapValueString(filter);

            stringFilterMap.forEach((key,value) ->{
                MatchQueryBuilder matchQueryBuilder = null;
                if (key.equals("cid3") || key.equals("brandId")){
                    matchQueryBuilder = QueryBuilders.matchQuery(key, value);
                }else{
                    matchQueryBuilder = QueryBuilders.matchQuery("specs."+ key +".keyword",value);
                }
                boolQueryBuilder.must(matchQueryBuilder);
            });
            searchQueryBuilder.withFilter(boolQueryBuilder);
        }

        //match通过值只能查询一个字段 和 multiMatch 通过值查询多个字段???
        searchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));
        //品牌和分类
        searchQueryBuilder.addAggregation(AggregationBuilders.terms("cid_agg").field("cid3"));
        searchQueryBuilder.addAggregation(AggregationBuilders.terms("brand_agg").field("brandId"));
        //设置高亮字段
        searchQueryBuilder.withHighlightBuilder(ESHighLightUtil.getHighlightBuilder("title"));
        //分页
        searchQueryBuilder.withPageable(PageRequest.of(page-1,10));
        return searchQueryBuilder;
    }


    /**
     * 获取品牌集合
     * @param aggregations
     * @return
     */
    private List<BrandEntity> getBrandList(Aggregations aggregations){
        Terms brand_agg = aggregations.get("brand_agg");
        List<String> brandIdList = brand_agg.getBuckets().stream().map(brandBucket -> brandBucket.getKeyAsNumber().intValue() + "")
                .collect(Collectors.toList());
        //通过品牌id集合去查询数据
        Result<List<BrandEntity>> brandResult = brandFeign.getBrandByIds(String.join(",", brandIdList));
        return brandResult.getData();
    }


    /**
     * 获取分类集合
     * @param aggregations
     * @return
     */
    private Map<Integer, List<CategoryEntity>> getCategoryList(Aggregations aggregations){
        Terms cid_agg = aggregations.get("cid_agg");
        List<? extends Terms.Bucket> cidBuckets = cid_agg.getBuckets();

        //获得热度最高cid
        List<Integer> cidHotArr = Arrays.asList(0);
        List<Long> maxCount = Arrays.asList(0L);

        Map<Integer, List<CategoryEntity>> map = new HashMap<>();

        List<String> cidList = cidBuckets.stream().map(cidbucket -> {
            Number keyAsNumber = cidbucket.getKeyAsNumber();

            if (cidbucket.getDocCount() > maxCount.get(0)){
                maxCount.set(0,cidbucket.getDocCount());
                cidHotArr.set(0,keyAsNumber.intValue());
            }
            return keyAsNumber.intValue() + "";
        }).collect(Collectors.toList());

        String cidsStr = String.join(",", cidList);
        Result<List<CategoryEntity>> caterogyResult = categoryFeign.getCategoryByIds(cidsStr);

        map.put(cidHotArr.get(0),caterogyResult.getData());

        return map;

    }



    @Override
    public Result<JSONObject> clearGoodsEsData() {
        IndexOperations indexOps = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if (indexOps.exists()){
            indexOps.delete();
            log.info("删除索引成功");
        }
        return this.setResultSuccess();
    }


    @Override
    public Result<JSONObject> initGoodsEsData() {

        IndexOperations ops = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if (!ops.exists()){
            ops.create();
            log.info("创建索引成功");
            ops.createMapping();
            log.info("创建映射成功");
        }
        List<GoodsDoc> goodsDocs = this.esGoodsInfo(new SpuDTO());
        elasticsearchRestTemplate.save(goodsDocs);
        return this.setResultSuccess();
    }

    private List<GoodsDoc> esGoodsInfo(SpuDTO spuDTO) {

        List<GoodsDoc> goodsDocs = new ArrayList<>();
//        spuDTO.setPage(1);
//        spuDTO.setRows(5);
        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(spuDTO);

        if(spuInfo.getCode() == HTTPStatus.OK){
            //SPU数据
            List<SpuDTO> spuList = spuInfo.getData();
            //遍历spu赋值
            spuList.stream().forEach(spu ->{

                GoodsDoc goodsDoc = new GoodsDoc();

                //可搜索的属性
                goodsDoc.setTitle(spu.getTitle());
                goodsDoc.setBrandName(spu.getBrandName());
                goodsDoc.setCategoryName(spu.getCategoryName());
                //spu信息填充
                goodsDoc.setId(spu.getId().longValue());
                goodsDoc.setSubTitle(spu.getSubTitle());
                goodsDoc.setBrandId(spu.getBrandId().longValue());
                goodsDoc.setCid1(spu.getCid1().longValue());
                goodsDoc.setCid2(spu.getCid2().longValue());
                goodsDoc.setCid3(spu.getCid3().longValue());
                goodsDoc.setCreateTime(spu.getCreateTime());

                //sku数据填充
                Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spu.getId());
               // Map<List<Long>,List<Map<String,Object>>> hashMap = new HashMap<>();
                List<Long> priceList = new ArrayList<>();
                List<Map<String,Object>> skuMap =null;
                if(skuResult.getCode() == HTTPStatus.OK){
                    List<SkuDTO> skuList = skuResult.getData();
                    skuMap = skuList.stream().map(sku ->{
                        Map<String, Object> map = new HashMap<>();
                        map.put("id",sku.getId());
                        map.put("title",sku.getTitle());
                        map.put("images",sku.getImages());
                        map.put("price",sku.getPrice());

                        priceList.add(sku.getPrice().longValue());
                        return map;
                    }).collect(Collectors.toList());
                }
                //hashMap.put(priceList,skuMap);
                goodsDoc.setPrice(priceList);
                goodsDoc.setSkus(JSONUtil.toJsonString(skuMap));

                //规格参数填充
                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setCid(spu.getCid3());
                Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamList(specParamDTO);
                Map<String, Object> specMap = new HashMap<>();

                if (specParamResult.getCode() == HTTPStatus.OK){
                    List<SpecParamEntity> paramsList = specParamResult.getData();

                    //通过spuid去查询detail里的通用属性和特有属性的值
                    Result<SpuDetailEntity> detailBySpuId = goodsFeign.getDetailBySpuId(spu.getId());
                    if (detailBySpuId.getCode() == HTTPStatus.OK){
                        SpuDetailEntity spuDetailData = detailBySpuId.getData();

                        //通用规格参数的值
                        String genericSpec = spuDetailData.getGenericSpec();
                        Map<String, String> genericSpecMap = JSONUtil.toMapValueString(genericSpec);

                        //特有规格参数的值
                        String specialSpec = spuDetailData.getSpecialSpec();
                        Map<String, List<String>> specialSpecMap = JSONUtil.toMapValueStrList(specialSpec);

                        paramsList.stream().forEach(param ->{
                            if (param.getGeneric()){
                                if (param.getNumeric() && param.getSearching()){
                                    specMap.put(param.getName(),this.chooseSegment(genericSpecMap.get(param.getId()+""),param.getSegments(),param.getUnit()));
                                }else {
                                    specMap.put(param.getName(),genericSpecMap.get(param.getId()+""));
                                }
                            }else{
                                specMap.put(param.getName(),specialSpecMap.get(param.getId()+""));
                            }
                        });
                    }
                }
                goodsDoc.setSpecs(specMap);
                goodsDocs.add(goodsDoc);
            });
        }
        return goodsDocs;
    }

    private String chooseSegment(String value, String segments, String unit) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : segments.split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + unit + "以上";
                }else if(begin == 0){
                    result = segs[1] + unit + "以下";
                }else{
                    result = segment + unit;
                }
                break;
            }
        }
        return result;
    }
}
