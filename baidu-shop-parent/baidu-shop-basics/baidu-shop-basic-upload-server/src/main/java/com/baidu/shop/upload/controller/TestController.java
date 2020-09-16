package com.baidu.shop.upload.controller;

/**
 * @program: baidu-shop-parent
 * @description: 测试
 * @author: Mr.Zheng
 * @create: 2020-09-11 20:58
 **/
public class TestController {

    public static void main(String[] args) {
        StringBuffer str1 = new StringBuffer("good");
        StringBuffer str2 = new StringBuffer("bad");

        str1 = change(str1,str2);
        System.out.println("str1=====" + str1);
        System.out.println("str2=====" + str2);
    }
    private static StringBuffer change(StringBuffer str1,StringBuffer str2){
        str2 = str1;
        System.out.println("change:str2=====" + str2);

        str1 = new StringBuffer("good word");
        System.out.println("change:str1=====" + str1);

        str2.append("new word");
        System.out.println("change111111:str2=====" + str2);
        return str1;
    }

}
