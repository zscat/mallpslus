package com.zscat.mallplus.sms.mapper;

import com.zscat.mallplus.sms.entity.SmsMsgresult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;


/**
 * 
 * @author cxh
 * @email 274567491@qq.com
 * @date 2019-07-12 14:58:48
 */

public interface SmsMsgresultMapper extends BaseMapper<SmsMsgresult> {

	SmsMsgresult get(Long id);

	List<SmsMsgresult> list(SmsMsgresult msgresult);

    int count(SmsMsgresult msgresult);

	int save(SmsMsgresult msgresult);

	int update(SmsMsgresult msgresult);

	int remove(Long id);

	int batchRemove(Integer[] ids);
}
