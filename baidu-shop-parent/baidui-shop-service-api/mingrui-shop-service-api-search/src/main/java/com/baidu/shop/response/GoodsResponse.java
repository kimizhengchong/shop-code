package com.baidu.shop.response;

import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.status.HTTPStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName GoodsResponse
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-09-21 14:35
 * @Version V1.0
 **/
@Data
@NoArgsConstructor
public class GoodsResponse extends Result<List<GoodsDoc>> {

    private Long total;

    private Long totalPage;

    private List<BrandEntity> brandList;

    private List<CategoryEntity> categoryList;

    public GoodsResponse(Long total,Long totalPage,List<BrandEntity> brandList,List<CategoryEntity>
            categoryList,List<GoodsDoc> goodsDocs){
        super(HTTPStatus.OK,HTTPStatus.OK + "",goodsDocs);
        this.total=total;
        this.totalPage=totalPage;
        this.brandList=brandList;
        this.categoryList=categoryList;
    }
}