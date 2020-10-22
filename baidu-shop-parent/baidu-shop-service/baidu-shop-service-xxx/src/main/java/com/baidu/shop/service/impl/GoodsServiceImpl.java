package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.component.MrRabbitMQ;
import com.baidu.shop.constant.MqMessageConstant;
import com.baidu.shop.mapper.*;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.entity.*;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: baidu-shop-parent
 * @description: 商品实现类
 * @author: Mr.Zheng
 * @create: 2020-09-07 15:03
 **/
@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private SpuMapper spuMapper;

    @Autowired
    private BrandService brandService;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private SpuDetaliMapper spuDetaliMapper;

    @Resource
    private StockMapper stockMapper;

    @Autowired
    private MrRabbitMQ mrRabbitMQ;


    @Override
    public Result<SkuEntity> getSkuById(Long skuId) {
        SkuEntity skuEntity = skuMapper.selectByPrimaryKey(skuId);
        return this.setResultSuccess(skuEntity);
    }

    //修改大字段回显
    @Override
    public Result<SpuDetailEntity> getDetailBySpuId(Integer spuId) {
        SpuDetailEntity spuDetailEntity = spuDetaliMapper.selectByPrimaryKey(spuId);
        return this.setResultSuccess(spuDetailEntity);
    }

    //回显sku规格参数
    @Override
    public Result<List<SkuDTO>> getSkuBySpuId(Integer spuId) {
        List<SkuDTO> list = skuMapper.selectSkuAndStockBySpuId(spuId);
        return this.setResultSuccess(list);
    }

    //修改
    @Override
    public Result<JSONObject> editGoods(SpuDTO spuDTO) {

        this.editGoodsTranscation(spuDTO);
        mrRabbitMQ.send(spuDTO.getId() + "", MqMessageConstant.SPU_ROUT_KEY_UPDATE);
        return this.setResultSuccess();
    }

    @Transactional
    public void editGoodsTranscation(SpuDTO spuDTO){
        //修改spu
        Date date = new Date();
        SpuEntity spuEntity = BaiduUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setLastUpdateTime(date);
        spuMapper.updateByPrimaryKeySelective(spuEntity);

        //修改spuDetail
        spuDetaliMapper.updateByPrimaryKeySelective(BaiduUtil.copyProperties(spuDTO.getSpuDetail(),SpuDetailEntity.class));
        //修改sku
        //通过spuid删除sku
        List<Long> skuArr = this.getSkuBySpuIds(spuDTO.getId());
        //删除
        skuMapper.deleteByIdList(skuArr);

        stockMapper.deleteByIdList(skuArr);
        List<SkuDTO> skus = spuDTO.getSkus();

        this.addSkuAndStock(spuDTO.getSkus(),spuDTO.getId(),date);
    }

    //删除
    @Override
    public Result<JSONObject> deleteGoods(Integer spuId) {

        if(ObjectUtil.isNull(spuId)) return setResultError("不能被删除");

        this.deleteGoodsTranscation(spuId);
        mrRabbitMQ.send(spuId + "", MqMessageConstant.SPU_ROUT_KEY_DELETE);
        return this.setResultSuccess();
    }

    @Transactional
    public void deleteGoodsTranscation(Integer spuId){

        //删除spu
        spuMapper.deleteByPrimaryKey(spuId);
        //删除spuDetail
        spuDetaliMapper.deleteByPrimaryKey(spuId);

        //删除sku
        List<Long> skuArr = this.getSkuBySpuIds(spuId);
        if(skuArr.size() > 0){
            //删除skus
            skuMapper.deleteByIdList(skuArr);
            //删除库存
            stockMapper.deleteByIdList(skuArr);
        }
    }


    @Transactional
    @Override
    public Result<JSONObject> saleableGoods(Integer ids,Integer saleables) {
        SpuEntity spuEntity = new SpuEntity();
        spuEntity.setId(ids);
        spuEntity.setSaleable(saleables);
        spuMapper.updateByPrimaryKeySelective(spuEntity);
        return this.setResultSuccess();
    }

//    @Transactional
//    @Override
//    public Result<JSONObject> saleableGoods(SpuDTO spuDTO) {
//        SpuEntity spuEntity = BaiduUtil.copyProperties(spuDTO, SpuEntity.class);
//        spuEntity.setId(spuDTO.getId());
//        if(spuEntity.getSaleable() == 1){
//            spuEntity.setSaleable(0);
//            spuMapper.updateByPrimaryKeySelective(spuEntity);
//            return this.setResultSuccess("下架成功");
//        }else{
//            spuEntity.setSaleable(1);
//            spuMapper.updateByPrimaryKeySelective(spuEntity);
//            return this.setResultSuccess("上架成功");
//        }
//    }

    //通过spuId查询sku规格参数
    private List<Long> getSkuBySpuIds(Integer spuId){
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);
        return skuEntities.stream().map(sku -> sku.getId()).collect(Collectors.toList());
    }


    //新增和修改方法
    private void addSkuAndStock(List<SkuDTO> skus,Integer spuId,Date date){
        skus.stream().forEach(skuDTO -> {
            SkuEntity skuEntity = BaiduUtil.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);
            //新增stock
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            stockMapper.insertSelective(stockEntity);
        });
    }

    //新增
    @Override
    public Result<JSONObject> saveGoods(SpuDTO spuDTO) {


        Integer spuId = this.saveGoodsTranscation(spuDTO);

        //发送消息
        mrRabbitMQ.send(spuId + "", MqMessageConstant.SPU_ROUT_KEY_SAVE);
        return this.setResultSuccess();
    }

    @Transactional
    public Integer saveGoodsTranscation(SpuDTO spuDTO){
        Date date = new Date();
        //新增spu
        SpuEntity spuEntity = BaiduUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setSaleable(1);
        spuEntity.setValid(1);
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        spuMapper.insertSelective(spuEntity);
        //新增spuDetali
        Integer spuEntityId = spuEntity.getId();
        SpuDetailEntity spuDetailEntity = BaiduUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class);
        spuDetailEntity.setSpuId(spuEntityId);
        spuDetaliMapper.insertSelective(spuDetailEntity);
        //新增sku、
        this.addSkuAndStock(spuDTO.getSkus(),spuEntity.getId(),date);
        return spuEntity.getId();
    }

    //查询
    @Override
    public Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO) {

        //分页
        if (ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows())){
            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());
        }
        //构建条件查询
        Example example = new Example(SpuEntity.class);
        //构建查询条件
        Example.Criteria criteria = example.createCriteria();
        if(StringUtil.isNotEmpty(spuDTO.getTitle())){
            criteria.andLike("title","%"+spuDTO.getTitle()+"%");
        }
        if(ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() != 2){
            criteria.andEqualTo("saleable",spuDTO.getSaleable());
        }
        if (ObjectUtil.isNotNull(spuDTO.getId())){
            criteria.andEqualTo("id",spuDTO.getId());
        }
        //排序
        if (ObjectUtil.isNotNull(spuDTO.getSort())){
            example.setOrderByClause(spuDTO.getOrderByClause());
        }
        List<SpuEntity> list = spuMapper.selectByExample(example);
        //可以有优化的空间
        List<SpuDTO> spuDTOList = list.stream().map(spuEntity -> {
            SpuDTO spuDTO1 = BaiduUtil.copyProperties(spuEntity, SpuDTO.class);

            //设置品牌名称
            BrandDTO brandDTO = new BrandDTO();
            brandDTO.setId(spuEntity.getBrandId());
            Result<PageInfo<BrandEntity>> brandInfo = brandService.getBrandInfo(brandDTO);

            if(ObjectUtil.isNotNull(brandInfo)){
                PageInfo<BrandEntity> data = brandInfo.getData();
                List<BrandEntity> list1 = data.getList();
                if(!list1.isEmpty() && list1.size() == 1){
                    spuDTO1.setBrandName(list1.get(0).getName());
                }
            }
            //分类名称
            String categoryName = categoryMapper.selectByIdList(
                    Arrays.asList(spuDTO1.getCid1(),spuDTO1.getCid2(),spuDTO1.getCid3()))
                    .stream().map(category -> category.getName()).collect(Collectors.joining("/"));
            spuDTO1.setCategoryName(categoryName);
            return spuDTO1;
        }).collect(Collectors.toList());

        PageInfo<SpuEntity> info = new PageInfo<>(list);

        return this.setResult(HTTPStatus.OK,String.valueOf(info.getTotal()),spuDTOList);
    }
}
