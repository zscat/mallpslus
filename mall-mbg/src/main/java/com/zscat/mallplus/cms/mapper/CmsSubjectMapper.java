package com.zscat.mallplus.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zscat.mallplus.cms.entity.CmsSubject;

/**
 * <p>
 * 专题表 Mapper 接口
 * </p>
 *
 * @author zscat
 * @since 2019-04-17
 */
public interface CmsSubjectMapper extends BaseMapper<CmsSubject> {

    int countByToday(Long memberId);
}
