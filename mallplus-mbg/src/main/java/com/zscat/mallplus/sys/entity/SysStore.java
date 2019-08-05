package com.zscat.mallplus.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author zscat
 * @since 2019-05-18
 */
@Data
@TableName("sys_store")
public class SysStore implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("sms_quantity")
    private Long smsQuantity;

    @TableField("register_type")
    private Integer registerType;

    @TableField("expire_time")
    private LocalDateTime expireTime;

    @TableField("try_time")
    private LocalDateTime tryTime;

    @TableField("contact_mobile")
    private String contactMobile;

    @TableField("address_province")
    private Long addressProvince;

    @TableField("buy_plan_times")
    private Long buyPlanTimes;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("is_checked")
    private Integer isChecked;

    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField("service_phone")
    private String servicePhone;

    @TableField("address_lat")
    private String addressLat;

    @TableField("contact_name")
    private String contactName;

    @TableField("delete_time")
    private LocalDateTime deleteTime;

    @TableField("diy_profile")
    private String diyProfile;

    @TableField("industry_two")
    private Long industryTwo;

    @TableField("is_star")
    private Integer isStar;

    @TableField("is_try")
    private Integer isTry;

    private String logo;

    @TableField("address_detail")
    private String addressDetail;

    @TableField("plan_id")
    private Long planId;

    @TableField("support_name")
    private String supportName;

    private String name;

    private Integer status;

    private Long uid;

    private Integer type;

    @TableField("contact_qq")
    private String contactQq;

    @TableField("address_lng")
    private String addressLng;

    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    @TableField("support_phone")
    private String supportPhone;

    @TableField("address_area")
    private Long addressArea;

    @TableField("contact_qrcode")
    private String contactQrcode;

    private String description;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("industry_one")
    private Long industryOne;

    @TableField("address_city")
    private Long addressCity;




}
