package com.zscat.mallplus.utils;

import com.baomidou.mybatisplus.annotation.TableField;

/**
 * @Auther: shenzhuan
 * @Date: 2019/5/19 02:16
 * @Description:
 */

public class BaseEntity {
    /**
     * 昵称
     */
    @TableField("store_id")
    private Integer storeId;

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }
}
