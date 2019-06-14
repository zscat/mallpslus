package com.zscat.mallplus.pms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zscat.mallplus.pms.entity.PmsSkuStock;
import com.zscat.mallplus.pms.mapper.PmsSkuStockMapper;
import com.zscat.mallplus.pms.service.IPmsSkuStockService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * sku的库存 服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-04-19
 */
@Service
public class PmsSkuStockServiceImpl extends ServiceImpl<PmsSkuStockMapper, PmsSkuStock> implements IPmsSkuStockService {
    @Resource
    PmsSkuStockMapper pmsSkuStockMapper;
    public PmsSkuStock getSku(Long id,String guide){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("product_id",id);
        queryWrapper.eq("sp1",guide);
        PmsSkuStock pmsSkuStock = pmsSkuStockMapper.selectOne(queryWrapper);
        return pmsSkuStock;
    }
    public  PmsSkuStock getSkuByCode(String code){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("sku_code",code);
        PmsSkuStock pmsSkuStock = pmsSkuStockMapper.selectOne(queryWrapper);
        return pmsSkuStock;
    }
}
