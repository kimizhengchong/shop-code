package com.baidu.shop.utils;

/**
 * @program: baidu-shop-parent
 * @description:
 * @author: Mr.Zheng
 * @create: 2020-08-31 20:23
 **/
public class StringUtil {

    //判断字符串类型不为空且不为null
    public static Boolean isNotEmpty(String str){

        return null != str && !"".equals(str);
    }

    //判断字符串类型为空或为null
    public static Boolean isEmpty(String str){

        return null == str || "".equals(str);
    }
}
