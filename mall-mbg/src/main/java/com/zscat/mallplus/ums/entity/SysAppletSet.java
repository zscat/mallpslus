package com.zscat.mallplus.ums.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zscat.mallplus.utils.BaseEntity;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author zscat
 * @since 2019-06-15
 */
@TableName("sys_applet_set")
public class SysAppletSet extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String appid;

    private String appsecret;

    private String mchid;

    private String paySignKey;

    private String certName;

    private String notifyUrl;

    /**
     * 下单通知模版
     */
    private String templateId1;

    /**
     * 支付成功模版
     */
    private String templateId2;

    private String templateId3;

    private String templateId4;

    private String templateId5;

    private String templateId6;



    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppsecret() {
        return appsecret;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

    public String getMchid() {
        return mchid;
    }

    public void setMchid(String mchid) {
        this.mchid = mchid;
    }

    public String getPaySignKey() {
        return paySignKey;
    }

    public void setPaySignKey(String paySignKey) {
        this.paySignKey = paySignKey;
    }

    public String getCertName() {
        return certName;
    }

    public void setCertName(String certName) {
        this.certName = certName;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getTemplateId1() {
        return templateId1;
    }

    public void setTemplateId1(String templateId1) {
        this.templateId1 = templateId1;
    }

    public String getTemplateId2() {
        return templateId2;
    }

    public void setTemplateId2(String templateId2) {
        this.templateId2 = templateId2;
    }

    public String getTemplateId3() {
        return templateId3;
    }

    public void setTemplateId3(String templateId3) {
        this.templateId3 = templateId3;
    }

    public String getTemplateId4() {
        return templateId4;
    }

    public void setTemplateId4(String templateId4) {
        this.templateId4 = templateId4;
    }

    public String getTemplateId5() {
        return templateId5;
    }

    public void setTemplateId5(String templateId5) {
        this.templateId5 = templateId5;
    }

    public String getTemplateId6() {
        return templateId6;
    }

    public void setTemplateId6(String templateId6) {
        this.templateId6 = templateId6;
    }



    @Override
    public String toString() {
        return "SysAppletSet{" +
        ", appid=" + appid +
        ", appsecret=" + appsecret +
        ", mchid=" + mchid +
        ", paySignKey=" + paySignKey +
        ", certName=" + certName +
        ", notifyUrl=" + notifyUrl +
        ", templateId1=" + templateId1 +
        ", templateId2=" + templateId2 +
        ", templateId3=" + templateId3 +
        ", templateId4=" + templateId4 +
        ", templateId5=" + templateId5 +
        ", templateId6=" + templateId6 +

        "}";
    }
}
