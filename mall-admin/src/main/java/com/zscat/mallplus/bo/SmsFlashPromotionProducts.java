package com.zscat.mallplus.bo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zscat.mallplus.pms.entity.PmsProduct;
import com.zscat.mallplus.sms.entity.SmsFlashPromotionProductRelation;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SmsFlashPromotionProducts implements Serializable {
    private Long id;
    private BigDecimal flashPromotionPrice;
    private Integer flashPromotionCount;
    private Integer flashPromotionLimit;
    private Integer sort;
    private PmsProduct product;
}
