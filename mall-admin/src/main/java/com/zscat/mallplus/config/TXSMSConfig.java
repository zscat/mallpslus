/**
 * Copyright (C),2015-2019,优易信息技术有限公司
 * FileName:TXSMSConfig
 * Author:  Administrator
 * Date:    2019/7/10 16:31
 * Description:腾讯短信接口基础配置
 * History:
 * <author>          <time>          <version>           <desc>
 * 作者姓名         修改时间           版本号             描述
 */
package com.zscat.mallplus.config;
import com.aliyun.oss.OSSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TXSMSConfig {
    @Value("${txsms.appid}")
    private int appId;
    @Value("${txsms.appkey}")
    private String appkey;
    @Value("${txsms.addsignurl}")
    private String addsignurl;
    @Value("${txsms.smsSign}")
    private String smsSign;
    @Value("${txsms.sig}")
    private String sig;
    @Value("${txsms.templeteid}")
    private int templeteid;
}
