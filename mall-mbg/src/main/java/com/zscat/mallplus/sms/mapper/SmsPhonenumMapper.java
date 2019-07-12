package com.zscat.mallplus.sms.mapper;

import com.zscat.mallplus.sms.entity.SmsPhonenum;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;


/**
 * 
 * @author cxh
 * @email 274567491@qq.com
 * @date 2019-07-12 14:58:54
 */

public interface SmsPhonenumMapper extends BaseMapper<SmsPhonenum> {

	SmsPhonenum get(Long id);

	List<SmsPhonenum> list(SmsPhonenum phonenum);

    int count(SmsPhonenum phonenum);

	int save(SmsPhonenum phonenum);

	int update(SmsPhonenum phonenum);

	int remove(Long id);

	int batchRemove(Integer[] ids);
}
