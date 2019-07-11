package com.zscat.mallplus.oms.vo;


import com.zscat.mallplus.oms.entity.OmsCartItem;
import com.zscat.mallplus.oms.entity.OmsOrderItem;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Auther: shenzhuan
 * @Date: 2019/6/1 13:54
 * @Description:
 */
@Data
public class CartMarkingVo implements Serializable{
    private List<OmsCartItem> cartList ;
    private List<OmsOrderItem> shopOrderGoodsList ;
    /**
     *
     * 首购礼
     * 类型1 第一单获取 2 所有订单获取
     */
    private int type ;
    /**
     * 新人券 1首次进入 2首次下单 3 首次支付
     * 满额发券 1 订单完成 2 支付完成
     * g购物发券  1 订单完成 2 支付完成
     * 手工改发券  1 订单完成 2 支付完成
     */
    private int scope ;
    private Long  ruleId ;
    private String  ruleIds ;
    private Long  memberId ;
    private String  code ;
    private String  marketingId ;
    private Long  memberCouponId;
    private BigDecimal payAmount;
    private String  openId ;
}
