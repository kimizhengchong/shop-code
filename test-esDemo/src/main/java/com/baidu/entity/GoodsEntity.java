package com.baidu.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @program: test-esDemo
 * @description: 商品实体类
 * @author: Mr.Zheng
 * @create: 2020-09-14 14:12
 **/
@Document(indexName = "goods",shards = 1,replicas = 0)
public class GoodsEntity {

    @Id
    private Long id;//主键

    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String title;//标题

    @Field(type = FieldType.Keyword)
    private String category;//分类

    @Field(type = FieldType.Keyword)
    private String brand;//品牌

    @Field(type = FieldType.Double)
    private Double price;//价格

    //index=false  是不需要参与索引搜索
    /**
     * 设置index为false的好处是 当您文档建立索引时,es将不必为该字段构建反向索引，
     * 同样,由于该字段在磁盘上将没有持久化的反向索引,因此您将 使用更少的磁盘空间
     */
    @Field(index = false,type = FieldType.Keyword)
    private String images;//图片地址

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "GoodsEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", price=" + price +
                ", images='" + images + '\'' +
                '}';
    }
}
