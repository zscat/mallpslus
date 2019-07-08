package com.zscat.mallplus.single;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zscat.mallplus.annotation.IgnoreAuth;
import com.zscat.mallplus.annotation.SysLog;
import com.zscat.mallplus.cms.entity.CmsSubject;
import com.zscat.mallplus.cms.entity.CmsSubjectCategory;
import com.zscat.mallplus.cms.entity.CmsSubjectComment;
import com.zscat.mallplus.cms.entity.CmsTopic;
import com.zscat.mallplus.cms.service.ICmsSubjectCategoryService;
import com.zscat.mallplus.cms.service.ICmsSubjectCommentService;
import com.zscat.mallplus.cms.service.ICmsSubjectService;
import com.zscat.mallplus.cms.service.ICmsTopicService;
import com.zscat.mallplus.pms.service.IPmsProductAttributeCategoryService;
import com.zscat.mallplus.pms.service.IPmsProductCategoryService;
import com.zscat.mallplus.pms.service.IPmsProductService;
import com.zscat.mallplus.sms.service.ISmsGroupService;
import com.zscat.mallplus.ums.entity.UmsMember;
import com.zscat.mallplus.ums.entity.UmsMemberLevel;
import com.zscat.mallplus.ums.entity.UmsRewardLog;
import com.zscat.mallplus.ums.mapper.UmsMemberMapper;
import com.zscat.mallplus.ums.mapper.UmsRewardLogMapper;
import com.zscat.mallplus.ums.service.IUmsMemberLevelService;
import com.zscat.mallplus.util.UserUtils;
import com.zscat.mallplus.utils.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther: shenzhuan
 * @Date: 2019/4/2 15:02
 * @Description:
 */
@RestController
@Api(tags = "CmsController", description = "内容关系管理")
@RequestMapping("/api/single/cms")
public class SingeCmsController extends ApiBaseAction {

    @Resource
    private ISmsGroupService groupService;
    @Resource
    private IUmsMemberLevelService memberLevelService;
    @Resource
    private IPmsProductService pmsProductService;
    @Resource
    private IPmsProductAttributeCategoryService productAttributeCategoryService;
    @Resource
    private IPmsProductCategoryService productCategoryService;
    @Resource
    private ICmsTopicService topicService;

    @Resource
    private ICmsSubjectCategoryService subjectCategoryService;
    @Resource
    private ICmsSubjectService subjectService;
    @Resource
    private ICmsSubjectCommentService commentService;
    @Resource
    private UmsMemberMapper memberMapper;
    @Resource
    private UmsRewardLogMapper rewardLogMapper;
    @IgnoreAuth
    @SysLog(MODULE = "cms", REMARK = "查询文章列表")
    @ApiOperation(value = "查询文章列表")
    @GetMapping(value = "/subject/list")
    public Object subjectList(CmsSubject subject,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                              @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        return new CommonResult().success(subjectService.page(new Page<CmsSubject>(pageNum, pageSize), new QueryWrapper<>(subject)));
    }

    @SysLog(MODULE = "cms", REMARK = "查询文章分类列表")
    @IgnoreAuth
    @ApiOperation(value = "查询文章分类列表")
    @GetMapping(value = "/subjectCategory/list")
    public Object cateList(CmsSubjectCategory subjectCategory,
                           @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                           @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        return new CommonResult().success(subjectCategoryService.page(new Page<CmsSubjectCategory>(pageNum, pageSize), new QueryWrapper<>(subjectCategory)));
    }

    @SysLog(MODULE = "cms", REMARK = "查询文章评论列表")
    @IgnoreAuth
    @ApiOperation(value = "查询文章评论列表")
    @GetMapping(value = "/subjectComment/list")
    public Object subjectList(CmsSubjectComment subjectComment,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                              @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        return new CommonResult().success(commentService.page(new Page<CmsSubjectComment>(pageNum, pageSize), new QueryWrapper<>(subjectComment)));
    }

    @SysLog(MODULE = "pms", REMARK = "查询首页推荐文章")
    @IgnoreAuth
    @ApiOperation(value = "查询首页推荐文章")
    @GetMapping(value = "/recommendSubjectList/list")
    public Object getRecommendSubjectList(
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {

        return new CommonResult().success(subjectService.getRecommendSubjectList(1,1));
    }
    @SysLog(MODULE = "cms", REMARK = "查询专题列表")
    @IgnoreAuth
    @ApiOperation(value = "查询专题列表")
    @GetMapping(value = "/topic/list")
    public Object subjectList(CmsTopic topic,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                              @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        return new CommonResult().success(topicService.page(new Page<CmsTopic>(pageNum, pageSize), new QueryWrapper<>(topic)));
    }
    @SysLog(MODULE = "pms", REMARK = "查询专题详情信息")
    @IgnoreAuth
    @GetMapping(value = "/topic/detail")
    @ApiOperation(value = "查询专题详情信息")
    public Object topicDetail(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        CmsTopic productResult = topicService.getById(id);
        return new CommonResult().success(productResult);
    }
    @SysLog(MODULE = "pms", REMARK = "查询文章详情信息")
    @IgnoreAuth
    @GetMapping(value = "/subject/detail")
    @ApiOperation(value = "查询文章详情信息")
    public Object subjectDetail(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        CmsTopic productResult = topicService.getById(id);
        return new CommonResult().success(productResult);
    }
    @SysLog(MODULE = "cms", REMARK = "创建文章")
    @ApiOperation(value = "创建文章")
    @PostMapping(value = "/createSubject")
    public Object createSubject(CmsSubject subject, BindingResult result) {
        CommonResult commonResult;
        UmsMember member = this.getCurrentMember();
        if (member.getMemberLevelId() > 0) {
            UmsMemberLevel memberLevel = memberLevelService.getById(member.getMemberLevelId());

            int subjectCounts = subjectService.countByToday(member.getId());
            if (subjectCounts > memberLevel.getArticlecount()) {
                commonResult = new CommonResult().failed("你今天已经有发" + memberLevel.getArticlecount() + "篇文章");
                return commonResult;
            }
        }
        subject.setSchoolId(member.getSchoolId());
        subject.setAreaId(member.getAreaId());
        subject.setMemberId(member.getId());
        boolean count = subjectService.save(subject);
        if (count) {
            commonResult = new CommonResult().success(count);
        } else {
            commonResult = new CommonResult().failed();
        }
        return commonResult;
    }
    @ApiOperation(value = "打赏文章")
    @PostMapping(value = "/reward")
    @SysLog(MODULE = "ums", REMARK = "打赏文章")
    public Object reward(@RequestParam(value = "articlelId", required = true) Long articlelId,
                         @RequestParam(value = "coin", required = true) int coin) {
        try {
            UmsMember member = UserUtils.getCurrentMember();
            if (member.getBlance().compareTo(new BigDecimal(coin))<0){
                return new CommonResult().failed("余额不够");
            }
            member.setBlance(member.getBlance().subtract(new BigDecimal(coin)));
            memberMapper.updateById(member);
            CmsSubject subject = subjectService.getById(articlelId);
            UmsMember remember = memberMapper.selectById(subject.getMemberId());
            if (remember!=null){
                subject.setReward(subject.getReward()+coin);
                subjectService.updateById(subject);
                remember.setBlance(remember.getBlance().add(new BigDecimal(coin)));
                memberMapper.updateById(remember);
                UmsRewardLog log = new UmsRewardLog();
                log.setCoin(coin);log.setSendMemberId(member.getId());
                log.setRecMemberId(remember.getId());log.setCreateTime(new Date());
                log.setObjid(articlelId);
                rewardLogMapper.insert(log);
            }
            return new CommonResult().success("打赏文章成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new CommonResult().failed("打赏文章失败");
        }
    }

}
