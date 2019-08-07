package com.zscat.mallplus.util;

import com.zscat.mallplus.pms.entity.PmsProduct;
import com.zscat.mallplus.pms.vo.SamplePmsProduct;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/8/6.
 */
public class GoodsUtils {
    public static List<SamplePmsProduct> sampleGoodsList(List<PmsProduct> list) {
        List<SamplePmsProduct> products = new ArrayList<>();
        for (PmsProduct product : list) {
            SamplePmsProduct en = new SamplePmsProduct();
            BeanUtils.copyProperties(product, en);
            products.add(en);
        }
        return products;
    }

    public static SamplePmsProduct sampleGoods(PmsProduct list) {
        SamplePmsProduct en = new SamplePmsProduct();
        BeanUtils.copyProperties(list, en);
        return en;
    }
}
