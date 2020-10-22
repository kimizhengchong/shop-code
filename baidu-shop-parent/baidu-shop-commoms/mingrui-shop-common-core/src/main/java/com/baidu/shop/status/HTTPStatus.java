package com.baidu.shop.status;

/**
 * @program: baidu-shop-parent
 * @description:
 * @author: Mr.Zheng
 * @create: 2020-08-27 20:23
 **/
public class HTTPStatus {

    public static final int OK = 200;//成功

    public static final int ERROR = 500;//失败

    public static final int PARAMS_VALIDATE_ERROR = 5002;//参数校验失败

    public static final int VALID_USER_PASSWORD_USER = 5003;//登录校验

    public static final int VERIFY_ERROR = 403;//COOKIE
}
