package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @program: baidu-shop-parent
 * @description: 规格实体类
 * @author: Mr.Zheng
 * @create: 2020-09-03 11:48
 **/
@Table(name = "tb_spec_group")
@Data
public class SpecGroupEntity {

    @Id
    private Integer id;

    private Integer cid;

    private String name;
}
