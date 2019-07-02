package com.zscat.mallplus.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zscat.mallplus.cms.entity.CmsSubject;
import com.zscat.mallplus.cms.mapper.CmsSubjectCategoryMapper;
import com.zscat.mallplus.cms.mapper.CmsSubjectMapper;
import com.zscat.mallplus.cms.service.ICmsSubjectService;
import com.zscat.mallplus.sms.entity.SmsHomeRecommendSubject;
import com.zscat.mallplus.sms.service.ISmsHomeRecommendSubjectService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 专题表 服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-04-17
 */
@Service
public class CmsSubjectServiceImpl extends ServiceImpl<CmsSubjectMapper, CmsSubject> implements ICmsSubjectService {


    @Resource
    private CmsSubjectMapper subjectMapper;
    @Resource
    private ISmsHomeRecommendSubjectService homeRecommendSubjectService;


    @Resource
    private CmsSubjectCategoryMapper subjectCategoryMapper;


    @Override
    public List<CmsSubject> getRecommendSubjectList(int pageNum, int pageSize) {
        List<SmsHomeRecommendSubject> brands = homeRecommendSubjectService.list(new QueryWrapper<>());
        List<Long> ids = brands.stream()
                .map(SmsHomeRecommendSubject::getId)
                .collect(Collectors.toList());
        return (List<CmsSubject>) subjectMapper.selectBatchIds(ids);
    }

    @Override
    public int countByToday(Long id){
       return subjectMapper.countByToday(id);
    }
}
