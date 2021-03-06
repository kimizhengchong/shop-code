package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserMapper;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.UserService;
import com.baidu.shop.utils.BCryptUtil;
import com.baidu.shop.utils.BaiduUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @ClassName UserServiceImpl
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-10-13 14:44
 * @Version V1.0
 **/
@RestController
@Slf4j
public class UserServiceImpl extends BaseApiService implements UserService {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private RedisRepository redisRepository;


    @Override
    public Result<JSONObject> checkValidCode(String phone, String code) {
        String redisValidCode = redisRepository.get("valid-code-" + phone);
        if (!code.equals(redisValidCode)) return this.setResultError("验证码输入错误");
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> register(UserDTO userDTO) {

        UserEntity userEntity = BaiduUtil.copyProperties(userDTO, UserEntity.class);
        userEntity.setPassword(BCryptUtil.hashpw(userEntity.getPassword(),BCryptUtil.gensalt()));
        userEntity.setCreated(new Date());
        userMapper.insertSelective(userEntity);

        return this.setResultSuccess();
    }

    @Override
    public Result<List<UserEntity>> checkUserNameOrPhone(String value, Integer type) {

        Example example = new Example(UserEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if (type != null && value != null){
            if(type == 1){
                criteria.andEqualTo("username",value);
            }else if(type == 2){
                criteria.andEqualTo("phone",value);
            }
        }

        List<UserEntity> userEntities = userMapper.selectByExample(example);
        return this.setResultSuccess(userEntities);
    }

    @Override
    public Result<JSONObject> sendValidCode(UserDTO userDTO) {

        //生成随机的六位验证码
        String code = (int)((Math.random() * 9 + 1) * 100000) +"";
        //打印发送的验证码信息
        log.debug("向手机号:{} 发送验证码:{}",userDTO.getPhone(),code);
        //发送短信验证码
        //LuosimaoDuanxinUtil.SendCode(userDTO.getPhone(),code);

        //语音验证码
        //LuosimaoDuanxinUtil.sendSpeak(userDTO.getPhone(),code);

        redisRepository.set("valid-code-" + userDTO.getPhone(),code);

        redisRepository.expire("valid-code-" + userDTO.getPhone(),120);

        return this.setResultSuccess();
    }
}
