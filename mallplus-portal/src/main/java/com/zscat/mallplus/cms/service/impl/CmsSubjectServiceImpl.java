package com.zscat.mallplus.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zscat.mallplus.cms.entity.CmsSubject;
import com.zscat.mallplus.cms.mapper.CmsSubjectCategoryMapper;
import com.zscat.mallplus.cms.mapper.CmsSubjectMapper;
import com.zscat.mallplus.cms.service.ICmsSubjectService;
import com.zscat.mallplus.sms.entity.SmsHomeRecommendSubject;
import com.zscat.mallplus.sms.service.ISmsHomeRecommendSubjectService;
import com.zscat.mallplus.ums.entity.UmsMember;
import com.zscat.mallplus.ums.entity.UmsRewardLog;
import com.zscat.mallplus.ums.mapper.UmsMemberMapper;
import com.zscat.mallplus.ums.mapper.UmsRewardLogMapper;
import com.zscat.mallplus.util.UserUtils;
import com.zscat.mallplus.utils.CommonResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
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
    private UmsMemberMapper memberMapper;
    @Resource
    private UmsRewardLogMapper rewardLogMapper;
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

    @Transactional
    @Override
    public Object reward(Long aid, int coin) {
        try {
            UmsMember member = UserUtils.getCurrentMember();
            if (member!=null && member.getBlance().compareTo(new BigDecimal(coin))<0){
                return new CommonResult().failed("余额不够");
            }
            member.setBlance(member.getBlance().subtract(new BigDecimal(coin)));
            memberMapper.updateById(member);
            CmsSubject subject = subjectMapper.selectById(aid);
            UmsMember remember = memberMapper.selectById(subject.getMemberId());
            if (remember!=null){
                subject.setReward(subject.getReward()+coin);
                subjectMapper.updateById(subject);
                remember.setBlance(remember.getBlance().add(new BigDecimal(coin)));
                memberMapper.updateById(remember);
                UmsRewardLog log = new UmsRewardLog();
                log.setCoin(coin);log.setSendMemberId(member.getId());
                log.setMemberIcon(member.getIcon());
                log.setMemberNickName(member.getNickname());
                log.setRecMemberId(remember.getId());log.setCreateTime(new Date());
                log.setObjid(aid);
                rewardLogMapper.insert(log);
            }
            return new CommonResult().success("打赏文章成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new CommonResult().failed("打赏文章失败");
        }
    }
}
