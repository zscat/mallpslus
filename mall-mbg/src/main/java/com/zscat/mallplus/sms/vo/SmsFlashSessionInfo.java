package com.zscat.mallplus.sms.vo;

import lombok.Data;

import java.util.Date;

@Data
public class SmsFlashSessionInfo {
    private Long id;
    private String name;
    private Date startTime;
    private Date endTime;
}
