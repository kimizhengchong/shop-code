package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.constant.MRshopConstant;
import com.baidu.shop.dto.Car;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.feign.GoodsCarFeign;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.CarService;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CarServiceImpl
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-10-19 19:43
 * @Version V1.0
 **/
@RestController
@Slf4j
public class CarServiceImpl extends BaseApiService implements CarService {

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private GoodsCarFeign goodsCarFeign;

    @Autowired
    private JwtConfig jwtConfig;


    @Override
    public Result<JSONObject> carNumUpdate(Long skuId, Integer type, String token) {

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Car car = redisRepository.getHash(MRshopConstant.GOODS_CAR_PRE + userInfo.getId(), skuId + "", Car.class);

            if (car != null){
                if (type == 1){
                    car.setNum(car.getNum() + 1);
                }else{
                    car.setNum(car.getNum() - 1);
                }
                redisRepository.setHash(MRshopConstant.GOODS_CAR_PRE + userInfo.getId(),car.getSkuId()+"",JSONUtil.toJsonString(car));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<List<Car>> userGoodsCar(String token) {

        List<Car> carList = new ArrayList<>();
        try {
            //获取当前登录用户
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            //通过用户id从redis获取购物车数据
            Map<String, String> stringMap = redisRepository.getHash(MRshopConstant.GOODS_CAR_PRE + userInfo.getId());

            stringMap.forEach((key,value) ->{
                carList.add(JSONUtil.toBean(value,Car.class));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess(carList);
    }

    @Override
    public Result<JSONObject> mergeCar(String clientCarList, String token) {
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(clientCarList);
        List<Car> carList = com.alibaba.fastjson.JSONObject.parseArray(jsonObject.getJSONArray("clientCarList").toJSONString(), Car.class);

        carList.stream().forEach(car -> {
            this.addCar(car,token);
        });

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> addCar(Car car,String token) {
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            //通过用户和商品id获取购物车中的数据
            Car userCarItem = redisRepository.getHash(MRshopConstant.GOODS_CAR_PRE + userInfo.getId(), car.getSkuId() + "", Car.class);
            log.debug("从redis中获取数据 : " + userCarItem);
            if (userCarItem == null) {
                //通过skuId查询sku详细信息
                log.debug("当前用户 {} 没有将sku : {} 添加到购物车",userInfo.getUsername(),car.getSkuId());
                Result<SkuEntity> skuResult = goodsCarFeign.getSkuById(car.getSkuId());
                if (skuResult.getCode() == 200) {

                    SkuEntity sku = skuResult.getData();

                    car.setUserId(userInfo.getId());
                    car.setTitle(sku.getTitle());
                    car.setPrice(sku.getPrice().longValue());
                    //判断images的值是否为空,如果为空的话就返回空,如果不为空的话通过,分隔取第一张图片即可
                    car.setImage(StringUtils.isEmpty(sku.getImages()) ? "" : sku.getImages().split(",")[0]);

                    car.setOwnSpec(sku.getOwnSpec());
                }

                boolean b = redisRepository.setHash(MRshopConstant.GOODS_CAR_PRE + userInfo.getId(), car.getSkuId() + "", JSONUtil.toJsonString(car));
                log.debug("添加到redis结果 : {} , hashkey : {} , mapkey : {} ,value :{}",b,MRshopConstant.GOODS_CAR_PRE + userInfo.getId(),car.getSkuId(),JSONUtil.toJsonString(car));

            }else{
                log.debug("当前用户 {} 以前添加过sku : {} 的数据,更改购物车对应的商品数量为 : {}",userInfo.getUsername(),car.getSkuId(),userCarItem.getNum() + car.getNum());
                userCarItem.setNum(userCarItem.getNum() + car.getNum());

                //这行代码可以优化,定义一个Car的变量,if和else的car对象重新复制,将此行代码提出去
                boolean b = redisRepository.setHash(MRshopConstant.GOODS_CAR_PRE + userInfo.getId(), car.getSkuId() + "", JSONUtil.toJsonString(userCarItem));
                log.debug("添加到redis结果 : {} , hashkey : {} , mapkey : {} ",b,MRshopConstant.GOODS_CAR_PRE + userInfo.getId(),car.getSkuId());
            }

        } catch (Exception e) {//进catch说明token有问题
            e.printStackTrace();
        }
        return this.setResultSuccess();
    }
}
