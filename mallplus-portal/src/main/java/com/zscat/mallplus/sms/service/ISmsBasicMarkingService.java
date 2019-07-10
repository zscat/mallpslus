package com.zscat.mallplus.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zscat.mallplus.sms.entity.SmsBasicMarking;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zscat
 * @since 2019-07-07
 */
public interface ISmsBasicMarkingService extends IService<SmsBasicMarking> {

    int updateStatus(Long id, Integer status, Integer bigType);

    List<SmsBasicMarking> matchGoodsMk(Long id);
}
