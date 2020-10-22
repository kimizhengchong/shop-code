package com.baidu.shop.business.impl;

import com.baidu.shop.business.PayService;
import com.baidu.shop.service.BaseApiService;
import org.springframework.stereotype.Controller;

/**
 * @ClassName PayServiceImpl
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-10-22 15:20
 * @Version V1.0
 **/
@Controller
public class PayServiceImpl extends BaseApiService implements PayService {

    @Override
    public void requestPay() {
        System.out.println("===============");
    }
}
