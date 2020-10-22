package com.baidu.shop.web;

import com.baidu.shop.base.Result;
import com.baidu.shop.business.UserOauthService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName UserOauthController
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-10-15 19:24
 * @Version V1.0
 **/
@RestController
@Api(tags = "用户认证接口")
public class UserOauthController extends BaseApiService {

    @Autowired
    private UserOauthService userOauthService;

    @Autowired
    private JwtConfig jwtConfig;

    @PostMapping(value = "oauth/login")
    @ApiOperation(value = "用户登录")
    public Result<JSONObject> login(@RequestBody UserEntity userEntity,
                                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){

        String token = userOauthService.login(userEntity,jwtConfig);

        if (ObjectUtil.isNull(token)) {
            return this.setResultError(HTTPStatus.VALID_USER_PASSWORD_USER,"用户名或密码错误");
        }
        //判断token是否为null
        //true：用户名或密码错误
        //false：将token放到cookie中
        CookieUtils.setCookie(httpServletRequest,httpServletResponse,
                jwtConfig.getCookieName(),token,jwtConfig.getCookieMaxAge(),true);


        return this.setResultSuccess();
    }

    @GetMapping(value = "oauth/verify")
    public Result<UserInfo> checkUserIsLogin(@CookieValue(value = "MRSHOP_TOKEN") String token,
                                             HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token,jwtConfig.getPublicKey());

            //可以解析token的证明用户是正确登录状态,重新生成token,这样登录状态就又刷新30分组过期
            String newToken = JwtUtils.generateToken(userInfo,jwtConfig.getPrivateKey(),jwtConfig.getExpire());

            //将新token写入cookie,过期时间延长了
            CookieUtils.setCookie(httpServletRequest,httpServletResponse,jwtConfig.getCookieName(),newToken,
                    jwtConfig.getCookieMaxAge(),true);

            return this.setResultSuccess(userInfo);
        } catch (Exception e) {//如果有异常 说明token有问题
            //e.printStackTrace();
            //新建http状态为用户验证失败,状态码为403
            return this.setResultError(HTTPStatus.VERIFY_ERROR,"用户失效");
        }
    }
}
