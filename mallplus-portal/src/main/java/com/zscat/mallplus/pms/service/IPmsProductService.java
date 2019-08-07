package com.zscat.mallplus.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zscat.mallplus.pms.entity.PmsBrand;
import com.zscat.mallplus.pms.entity.PmsProduct;
import com.zscat.mallplus.pms.vo.GoodsDetailResult;
import com.zscat.mallplus.pms.vo.PmsProductAndGroup;
import com.zscat.mallplus.pms.vo.PmsProductParam;
import com.zscat.mallplus.pms.vo.PmsProductResult;

import java.util.List;

/**
 * <p>
 * 商品信息 服务类
 * </p>
 *
 * @author zscat
 * @since 2019-04-19
 */
public interface IPmsProductService extends IService<PmsProduct> {

    PmsProductAndGroup getProductAndGroup(Long id);

    PmsProduct getUpdateInfo(Long id);

    Object initGoodsRedis();
    GoodsDetailResult getGoodsRedisById(Long id) ;

    List<PmsBrand> getRecommendBrandList(int pageNum, int pageSize) ;
    List<PmsProduct> getNewProductList(int pageNum, int pageSize) ;
    List<PmsProduct> getHotProductList(int pageNum, int pageSize) ;

    Integer countGoodsByToday(Long id);
}
