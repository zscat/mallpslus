package com.zscat.mallplus.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

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

    private Integer uid;

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
    private Integer id;

    @TableField("industry_one")
    private Long industryOne;

    @TableField("address_city")
    private Long addressCity;


    public Long getSmsQuantity() {
        return smsQuantity;
    }

    public void setSmsQuantity(Long smsQuantity) {
        this.smsQuantity = smsQuantity;
    }

    public Integer getRegisterType() {
        return registerType;
    }

    public void setRegisterType(Integer registerType) {
        this.registerType = registerType;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public LocalDateTime getTryTime() {
        return tryTime;
    }

    public void setTryTime(LocalDateTime tryTime) {
        this.tryTime = tryTime;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    public Long getAddressProvince() {
        return addressProvince;
    }

    public void setAddressProvince(Long addressProvince) {
        this.addressProvince = addressProvince;
    }

    public Long getBuyPlanTimes() {
        return buyPlanTimes;
    }

    public void setBuyPlanTimes(Long buyPlanTimes) {
        this.buyPlanTimes = buyPlanTimes;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Integer isChecked) {
        this.isChecked = isChecked;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getServicePhone() {
        return servicePhone;
    }

    public void setServicePhone(String servicePhone) {
        this.servicePhone = servicePhone;
    }

    public String getAddressLat() {
        return addressLat;
    }

    public void setAddressLat(String addressLat) {
        this.addressLat = addressLat;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public LocalDateTime getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(LocalDateTime deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getDiyProfile() {
        return diyProfile;
    }

    public void setDiyProfile(String diyProfile) {
        this.diyProfile = diyProfile;
    }

    public Long getIndustryTwo() {
        return industryTwo;
    }

    public void setIndustryTwo(Long industryTwo) {
        this.industryTwo = industryTwo;
    }

    public Integer getIsStar() {
        return isStar;
    }

    public void setIsStar(Integer isStar) {
        this.isStar = isStar;
    }

    public Integer getIsTry() {
        return isTry;
    }

    public void setIsTry(Integer isTry) {
        this.isTry = isTry;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getSupportName() {
        return supportName;
    }

    public void setSupportName(String supportName) {
        this.supportName = supportName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContactQq() {
        return contactQq;
    }

    public void setContactQq(String contactQq) {
        this.contactQq = contactQq;
    }

    public String getAddressLng() {
        return addressLng;
    }

    public void setAddressLng(String addressLng) {
        this.addressLng = addressLng;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getSupportPhone() {
        return supportPhone;
    }

    public void setSupportPhone(String supportPhone) {
        this.supportPhone = supportPhone;
    }

    public Long getAddressArea() {
        return addressArea;
    }

    public void setAddressArea(Long addressArea) {
        this.addressArea = addressArea;
    }

    public String getContactQrcode() {
        return contactQrcode;
    }

    public void setContactQrcode(String contactQrcode) {
        this.contactQrcode = contactQrcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getIndustryOne() {
        return industryOne;
    }

    public void setIndustryOne(Long industryOne) {
        this.industryOne = industryOne;
    }

    public Long getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(Long addressCity) {
        this.addressCity = addressCity;
    }


}
