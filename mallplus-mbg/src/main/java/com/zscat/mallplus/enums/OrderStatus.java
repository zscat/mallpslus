package com.zscat.mallplus.enums;

/**
 * @Auther: Tiger
 * @Date: 2019-04-26 16:04
 * @Description:
 */
public enum OrderStatus {


    //   订单状态：12->待付款；2->待发货；3->已发货；4->已完成；5->售后订单 6->已关闭；
    INIT(12),//待付款
    TO_DELIVER(2),//待发货
    DELIVERED(3),  // 待收货
    TRADE_SUCCESS(4), // 已完成
    REFUND(7),  // 已退款
    RIGHT_APPLY(5), // 维权中
    RIGHT_APPLYF_SUCCESS(6), // 维权已完成
    //    CANCELED(7),
    CLOSED(8), // 已关闭 // 已取消 统一
    INVALID(9),//无效订单
    DELETED(10),//已删除
    PARTDELIVE(11);//部分发货状态


    private int value;

    private OrderStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}
