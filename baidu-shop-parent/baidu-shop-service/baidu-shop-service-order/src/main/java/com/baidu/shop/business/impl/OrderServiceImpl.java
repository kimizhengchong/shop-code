package com.baidu.shop.business.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.business.OrderService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.constant.MRshopConstant;
import com.baidu.shop.dto.Car;
import com.baidu.shop.dto.OrderDTO;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.OrderDetailEntity;
import com.baidu.shop.entity.OrderEntity;
import com.baidu.shop.entity.OrderStatusEntity;
import com.baidu.shop.mapper.OrderDetailMapper;
import com.baidu.shop.mapper.OrderMapper;
import com.baidu.shop.mapper.OrderStatusMapper;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.IdWorker;
import com.baidu.shop.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName OrderServiceImpl
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-10-21 14:49
 * @Version V1.0
 **/
@RestController
public class OrderServiceImpl extends BaseApiService implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private JwtConfig jwtConfig;

    @Resource
    private RedisRepository redisRepository;



    @Transactional
    @Override
    public Result<String> createOrder(OrderDTO orderDTO, String token) {

        //通过雪花算法生成订单id
        long orderId = idWorker.nextId();

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            OrderEntity orderEntity = new OrderEntity();

            Date date = new Date();
            orderEntity.setOrderId(orderId);
            orderEntity.setUserId(userInfo.getId() + "");
            //orderEntity.setActualPay();
            //orderEntity.setTotalPay();
            orderEntity.setSourceType(1);//pc端写的固定,如果项目健全了之后,这个值应该是常量
            orderEntity.setInvoiceType(1);//发票也是写死
            orderEntity.setBuyerRate(1);//用户是否评论  1:没有
            orderEntity.setBuyerNick(userInfo.getUsername());
            orderEntity.setBuyerMessage("买家留言");
            orderEntity.setPaymentType(orderDTO.getPayType());
            orderEntity.setCreateTime(date);

            List<Long> longs = Arrays.asList(0L);

            //detail
            List<OrderDetailEntity> orderDetailList = Arrays.asList(orderDTO.getSkuIds().split(",")).stream().map(skuIdStr -> {
                //在一个类(匿名的)的方法(匿名方法)中操作
                //skuIdStr
                //通过skuId查询redis --> sku数据
                Car car = redisRepository.getHash(MRshopConstant.GOODS_CAR_PRE + userInfo.getId(), skuIdStr, Car.class);
                if(car == null){
                    throw new RuntimeException("数据异常");
                }
                OrderDetailEntity orderDetailEntity = new OrderDetailEntity();
                orderDetailEntity.setSkuId(Long.valueOf(skuIdStr));
                orderDetailEntity.setTitle(car.getTitle());
                orderDetailEntity.setPrice(car.getPrice());
                orderDetailEntity.setNum(car.getNum());
                orderDetailEntity.setImage(car.getImage());
                orderDetailEntity.setOrderId(orderId);
                //totalPrice += car.getPrice() * car.getNum();
                longs.set(0,car.getPrice() * car.getNum() + longs.get(0));

                return orderDetailEntity;
            }).collect(Collectors.toList());

            orderEntity.setActualPay(longs.get(0));
            orderEntity.setTotalPay(longs.get(0));

            //status
            OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
            orderStatusEntity.setCreateTime(date);
            orderStatusEntity.setOrderId(orderId);
            orderStatusEntity.setStatus(1);//已经创建订单,但是没有支付

            //入库
            orderMapper.insertSelective(orderEntity);
            orderDetailMapper.insertList(orderDetailList);
            orderStatusMapper.insertSelective(orderStatusEntity);

            //通过用户id和skuid删除购物车中的数据
            Arrays.asList(orderDTO.getSkuIds().split(",")).stream().forEach(skuidStr -> {
                redisRepository.delHash(MRshopConstant.GOODS_CAR_PRE + userInfo.getId(),skuidStr);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


        return this.setResult(HTTPStatus.OK,"",orderId + "");
    }
}
