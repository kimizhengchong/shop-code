package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.shop.mapper.SpecParamMapper;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.SpecGroupService;
import com.baidu.shop.utils.BaiduUtil;
import com.baidu.shop.utils.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: baidu-shop-parent
 * @description: 规格实现类
 * @author: Mr.Zheng
 * @create: 2020-09-03 12:00
 **/
@RestController
public class SpecGroupImpl extends BaseApiService implements SpecGroupService {

    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private SpecParamMapper specParamMapper;

    @Override
    public Result<List<SpecGroupEntity>> getSpecGroup(SpecGroupDTO specGroupDTO) {

        //通过分类id查询
        Example example = new Example(SpecGroupEntity.class);


        if(ObjectUtil.isNotNull(specGroupDTO.getCid())) example.createCriteria().andEqualTo("cid",specGroupDTO.getCid());
        List<SpecGroupEntity> list = specGroupMapper.selectByExample(example);

        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<JSONObject> add(SpecGroupDTO specGroupDTO) {

        specGroupMapper.insertSelective(BaiduUtil.copyProperties(specGroupDTO,SpecGroupEntity.class));
        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> edit(SpecGroupDTO specGroupDTO) {

        specGroupMapper.updateByPrimaryKeySelective(BaiduUtil.copyProperties(specGroupDTO,SpecGroupEntity.class));

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> delete(Integer id) {

        //删除组id之前先查询该组下面有没有参数,没有参数可以删,有参数直接return响应错误内容
        //通过id查询当前组下面的参数
        Example exampleParam = new Example(SpecParamEntity.class);
        exampleParam.createCriteria().andEqualTo("groupId",id);
        List<SpecParamEntity> list = specParamMapper.selectByExample(exampleParam);
        //如果查询出来有数据,不能被删除
        if (list.size() > 0){
            return this.setResultError("该组下被参数绑定 不能被删除");
        }else{
            specGroupMapper.deleteByPrimaryKey(id);
        }
        return this.setResultSuccess();
    }

    //===================================================参数实现类

    @Override
    public Result<SpecParamEntity> getSpecParamList(SpecParamDTO specParamDTO) {

        if (ObjectUtil.isNull(specParamDTO.getGroupId())){
            return this.setResultError("规格组id为空");
        }
        Example example = new Example(SpecParamEntity.class);
        example.createCriteria().andEqualTo("groupId",specParamDTO.getGroupId());
        List<SpecParamEntity> list = specParamMapper.selectByExample(example);

        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<JSONObject> add(SpecParamDTO specParamDTO) {

        specParamMapper.insertSelective(BaiduUtil.copyProperties(specParamDTO,SpecParamEntity.class));

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> edit(SpecParamDTO specParamDTO) {
        specParamMapper.updateByPrimaryKeySelective(BaiduUtil.copyProperties(specParamDTO,SpecParamEntity.class));
        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> deleteParam(Integer id) {

        specParamMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }


}
