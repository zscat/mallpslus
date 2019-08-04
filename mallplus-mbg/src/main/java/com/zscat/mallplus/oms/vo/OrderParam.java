package com.zscat.mallplus.oms.vo;

import lombok.Data;

/**
 * 生成订单时传入的参数
 * https://github.com/shenzhuan/mallplus on 2018/8/30.
 */
@Data
public class OrderParam {
    String page;
    String formId;
    private Integer total;
    String platform = "2";
    //收货地址id
    private Long addressId;
    //优惠券id
    private Long couponId;
    private Long memberId;
    //使用的积分数
    private Integer useIntegration;
    //支付方式
    private Integer payType =1;
    private Integer offline;// 0 送货 1 自取
    private String content;
    private String cartId;
    private String cartIds;
    private String type; // 1 商品详情 2 勾选购物车 3全部购物车的商品

    private Long skuId;
    private Long goodsId;
    private Long groupId;
    // 1 发起拼团 2 参与拼团
    private Integer groupType;
    private Long mgId =0l;


}
