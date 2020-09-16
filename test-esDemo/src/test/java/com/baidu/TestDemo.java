package com.baidu;

import com.baidu.entity.GoodsEntity;
import com.baidu.repository.GoodsEsRepository;
import com.baidu.util.ESHighLightUtil;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: test-esDemo
 * @description: 测试类
 * @author: Mr.Zheng
 * @create: 2020-09-14 14:29
 **/
//让测试在Spring容器环境下执行
@RunWith(SpringRunner.class)
//声明启动类,当测试方法运行的时候会帮我们自动启动容器
@SpringBootTest(classes = { TestEsDemoApplicationStart.class})
public class TestDemo {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private GoodsEsRepository goodsEsRepository;

    /**
     *创建索引
     */
    @Test
    public void createGoodsIndex(){
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(IndexCoordinates.of("test"));
        //创建索引
        indexOperations.create();
        System.out.println(indexOperations.exists()?"索引创建成功":"索引创建失败");
    }


    /**
     * 创建mapping
     */
    @Test
    public void createGoodsMapping(){
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsEntity.class);
        indexOperations.createMapping(GoodsEntity.class);
        System.out.println("映射创建成功");
    }

    /**
     * 删除索引
     */
    @Test
    public void deleteGoodsIndex(){
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsEntity.class);

        indexOperations.delete();
        System.out.println("删除索引成功");
    }

    /**
     * 新增文档
     */
    @Test
    public void saveData(){
        GoodsEntity goodsEntity = new GoodsEntity();
        goodsEntity.setId(1L);
        goodsEntity.setTitle("Iphone");
        goodsEntity.setBrand("苹果");
        goodsEntity.setCategory("手机");
        goodsEntity.setPrice(5000D);
        goodsEntity.setImages("apple.jpg");

        goodsEsRepository.save(goodsEntity);
        System.out.println("新增成功");
    }


    /*
   批量新增文档
    */
    @Test
    public void saveAllData(){

        GoodsEntity entity = new GoodsEntity();
        entity.setId(2L);
        entity.setBrand("苹果");
        entity.setCategory("手机");
        entity.setImages("pingguo.jpg");
        entity.setPrice(5000D);
        entity.setTitle("iphone11手机");

        GoodsEntity entity2 = new GoodsEntity();
        entity2.setId(3L);
        entity2.setBrand("三星");
        entity2.setCategory("手机");
        entity2.setImages("sanxing.jpg");
        entity2.setPrice(3000D);
        entity2.setTitle("w2019手机");

        GoodsEntity entity3 = new GoodsEntity();
        entity3.setId(4L);
        entity3.setBrand("华为");
        entity3.setCategory("手机");
        entity3.setImages("huawei.jpg");
        entity3.setPrice(4000D);
        entity3.setTitle("华为mate30手机");

        goodsEsRepository.saveAll(Arrays.asList(entity,entity2,entity3));

        System.out.println("批量新增成功");
    }

    /*
  更新文档
   */
    @Test
    public void updateData(){

        GoodsEntity entity = new GoodsEntity();
        entity.setId(1L);
        entity.setBrand("小米");
        entity.setCategory("手机");
        entity.setImages("xiaomi.jpg");
        entity.setPrice(1000D);
        entity.setTitle("小米3");

        goodsEsRepository.save(entity);

        System.out.println("修改成功");
    }


    /*
  查询所有
   */
    @Test
    public void searchAll(){
        //查询总条数
        long count = goodsEsRepository.count();
        System.out.println(count);
        //查询所有数据
        Iterable<GoodsEntity> all = goodsEsRepository.findAll();
        all.forEach(goods -> {
            System.out.println(goods);
        });
    }

    /*
   条件查询
    */
    @Test
    public void searchByParam(){

        List<GoodsEntity> allByAndTitle = goodsEsRepository.findAllByAndTitle("手机");
        System.out.println(allByAndTitle);

        System.out.println("===============================");
        List<GoodsEntity> byAndPriceBetween = goodsEsRepository.findByAndPriceBetween(1000D, 3000D);
        System.out.println(byAndPriceBetween);

    }

    /*
    自定义查询
     */
    @Test
    public void customizeSearch(){

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("title","华为"))
                        .must(QueryBuilders.rangeQuery("price").gte(1000).lte(10000))
        );

        //排序
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));

        //分页
        //当前页 -1
        queryBuilder.withPageable(PageRequest.of(0,10));

        SearchHits<GoodsEntity> search = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);

        search.getSearchHits().stream().forEach(hit -> {
            System.out.println(hit.getContent());
        });

    }

    /*
   高亮
    */
    @Test
    public void customizeSearchHighLight(){

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //构建高亮查询
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field title = new HighlightBuilder.Field("title");
        title.preTags("<span style=color:red'>");
        title.postTags("</span>");
        highlightBuilder.field(title);

        queryBuilder.withHighlightBuilder(highlightBuilder);//设置高亮


        queryBuilder.withQuery(
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("title","华为手机"))
                        .must(QueryBuilders.rangeQuery("price").gte(1000).lte(10000))
        );

        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        queryBuilder.withPageable(PageRequest.of(0,2));

        SearchHits<GoodsEntity> search = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);

        List<SearchHit<GoodsEntity>> searchHits = search.getSearchHits();

        //重新设置title
        List<SearchHit<GoodsEntity>> result = searchHits.stream().map(hit -> {
            Map<String, List<String>> highlightFields = hit.getHighlightFields();
            hit.getContent().setTitle(highlightFields.get("title").get(0));
            return hit;
        }).collect(Collectors.toList());
        System.out.println(result);
    }

    @Test
    public void customizeSearchHighLightUtil(){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //构建高亮查询
        //设置高亮
        queryBuilder.withHighlightBuilder(ESHighLightUtil.getHighlightBuilder("title"));

        queryBuilder.withQuery(
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("title","华为手机"))
                        .must(QueryBuilders.rangeQuery("price").gte(1000).lte(10000))
        );

        SearchHits<GoodsEntity> search = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);

        List<SearchHit<GoodsEntity>> searchHits = search.getSearchHits();

        //重新设置title
        List<SearchHit<GoodsEntity>> highLightHit = ESHighLightUtil.getHighLightHit(searchHits);

        System.out.println(highLightHit);
    }

    /**
     * 聚合为桶  也就是分组
     */
    @Test
    public void searchAgg(){

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.addAggregation(
                AggregationBuilders.terms("brand_agg").field("brand")
        );

        SearchHits<GoodsEntity> search = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);

        Aggregations aggregations = search.getAggregations();

        //terms 是Aggregation的子类
        //Aggregation brand_agg = aggregations.get("brand_agg");/
        Terms terms = aggregations.get("brand_agg");

        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        buckets.forEach(bucket -> {
            System.out.println(bucket.getKeyAsString() + ":" + bucket.getDocCount());
        });
        System.out.println(search);
    }
    /*
      聚合函数
       */
    @Test
    public void searchAggMethod(){

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.addAggregation(
                AggregationBuilders.terms("brand_agg")
                        .field("brand")
                        //聚合函数
                        .subAggregation(AggregationBuilders.max("max_price").field("price"))
        );

        SearchHits<GoodsEntity> search = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);

        Aggregations aggregations = search.getAggregations();

        Terms terms = aggregations.get("brand_agg");

        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        buckets.forEach(bucket -> {
            System.out.println(bucket.getKeyAsString() + ":" + bucket.getDocCount());

            //获取聚合
            Aggregations aggregations1 = bucket.getAggregations();
            //得到map
            Map<String, Aggregation> map = aggregations1.asMap();
            //需要强转,Aggregations是一个类 Terms是他的子类,Aggregation是一个接口Max是他的子接口,而且Max是好几个接口的子接口
            Max max_price = (Max) map.get("max_price");
            System.out.println(max_price.getValue());
        });
    }
}
