/**
 * Copyright (C),2015-2019,优易信息技术有限公司
 * FileName:TXSMSController
 * Author:  Administrator
 * Date:    2019/7/10 17:04
 * Description:腾讯短信平台控制器
 * History:
 * <author>          <time>          <version>           <desc>
 * 作者姓名         修改时间           版本号             描述
 */
package com.zscat.mallplus.sys.controller;

import java.io.IOException;

import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.zscat.mallplus.annotation.SysLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

//腾讯云SMS工具
@Slf4j
@Api(value = "短信管理", description = "", tags = {"短信管理"})
@RestController
@RequestMapping("/sms")
public class TXSMSController {
    /**
     * 参数
     */
    @Value("${txsms.appid}")
    public  int appId;
    @Value("${txsms.appkey}")
    public  String appkey;
    @Value("${txsms.addsignurl}")
    public  String addsign;
    @Value("${txsms.smsSign}")
    public  String smsSign;
    @Value("${txsms.sig}")
    public  String sig;
    @Value("${txsms.templeteid}")
    public  int templeteId;

    public   String addsignurl = addsign+"add_sign";//签名API url

    @SysLog(MODULE = "sms", REMARK = "单人短信")
    @ApiOperation("单人短信")
    @PostMapping(value = "/signlesms")
    public  String SendSMSSignle(String[] phoneNums,String code) throws HTTPException, JSONException, IOException {
        // TODO Auto-generated method stub
        String[] params = {code};//数组具体的元素个数和模板中变量个数必须一致，例如事例中templateId:5678对应一个变量，参数数组中元素个数也必须是一个
        SmsSingleSender ssender = new SmsSingleSender(appId, appkey);
        SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNums[0],
                templeteId, params, smsSign, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
        System.out.println(result);
        return result.toString();
    }

    @SysLog(MODULE = "sms", REMARK = "群发短信")
    @ApiOperation("群发短信")
    @PostMapping(value = "/multisms")
    public  String SendSMSMultisms(@RequestParam(value = "phoneNums") String phoneNums, @RequestParam(value = "code") String code) throws HTTPException, IOException {
        String[] params = {code};
        String[] phones = phoneNums.split(",");
        SmsMultiSender smsMultiSender = new SmsMultiSender(appId,appkey);
        SmsMultiSenderResult result = smsMultiSender.sendWithParam("86",phones,
                templeteId,params,smsSign,"","");
        return result.toString();
    }

}

