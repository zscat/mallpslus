package com.zscat.mallplus.single;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zscat.mallplus.annotation.IgnoreAuth;
import com.zscat.mallplus.annotation.SysLog;
import com.zscat.mallplus.cms.entity.CmsSubject;
import com.zscat.mallplus.cms.entity.CmsSubjectCategory;
import com.zscat.mallplus.cms.mapper.CmsSubjectCategoryMapper;
import com.zscat.mallplus.cms.mapper.CmsSubjectMapper;
import com.zscat.mallplus.cms.service.ISysAreaService;
import com.zscat.mallplus.cms.service.ISysSchoolService;
import com.zscat.mallplus.pms.entity.PmsFavorite;
import com.zscat.mallplus.pms.entity.PmsProduct;
import com.zscat.mallplus.pms.entity.PmsProductAttributeCategory;
import com.zscat.mallplus.pms.mapper.PmsProductAttributeCategoryMapper;
import com.zscat.mallplus.pms.mapper.PmsProductMapper;
import com.zscat.mallplus.pms.service.IPmsFavoriteService;
import com.zscat.mallplus.pms.service.IPmsProductService;
import com.zscat.mallplus.pms.vo.SamplePmsProduct;
import com.zscat.mallplus.sys.entity.SysArea;
import com.zscat.mallplus.sys.entity.SysSchool;
import com.zscat.mallplus.sys.entity.SysStore;
import com.zscat.mallplus.sys.mapper.SysAreaMapper;
import com.zscat.mallplus.sys.mapper.SysSchoolMapper;
import com.zscat.mallplus.sys.mapper.SysStoreMapper;
import com.zscat.mallplus.ums.entity.UmsEmployInfo;
import com.zscat.mallplus.ums.entity.UmsMember;
import com.zscat.mallplus.ums.mapper.UmsEmployInfoMapper;
import com.zscat.mallplus.ums.mapper.UmsRewardLogMapper;
import com.zscat.mallplus.ums.service.IUmsMemberMemberTagRelationService;
import com.zscat.mallplus.ums.service.IUmsMemberService;
import com.zscat.mallplus.ums.service.RedisService;
import com.zscat.mallplus.ums.service.impl.RedisUtil;
import com.zscat.mallplus.util.GoodsUtils;
import com.zscat.mallplus.util.UserUtils;
import com.zscat.mallplus.utils.CommonResult;
import com.zscat.mallplus.vo.Rediskey;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Auther: shenzhuan
 * @Date: 2019/4/2 15:02
 * @Description:
 */
@RestController
@Api(tags = "TestController", description = "测试")
@RequestMapping("/test")
public class TestController extends ApiBaseAction {

    @Resource
    private ISysSchoolService schoolService;
    @Resource
    private IUmsMemberService memberService;
    @Resource
    private ISysAreaService areaService;
    @Resource
    private IUmsMemberMemberTagRelationService memberTagService;
    @Resource
    private UmsRewardLogMapper rewardLogMapper;
    @Resource
    private UmsEmployInfoMapper employInfoMapper;
    @Resource
    private SysStoreMapper storeMapper;
    @Resource
    private PmsProductMapper productMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private IPmsProductService pmsProductService;
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private IPmsFavoriteService favoriteService;
    @Resource
    private PmsProductAttributeCategoryMapper productAttributeCategoryMapper;
    @Resource
    CmsSubjectMapper subjectMapper;
    @Resource
    SysSchoolMapper schoolMapper;
    @Resource
    SysAreaMapper sysAreaMapper;
    @Resource
    CmsSubjectCategoryMapper categoryMapper;

    @ApiOperation("获取会员详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public Object detail() {
        List<SysArea> areas = sysAreaMapper.selectList(new QueryWrapper<>());
        List<SysSchool> schools = schoolMapper.selectList(new QueryWrapper<>());
        List<CmsSubject> list = subjectMapper.selectList(new QueryWrapper<>());
        for (CmsSubject subject : list){
            Random r = new Random();  Integer a = r.nextInt(100);
            Integer c = r.nextInt(3);
            Integer d = r.nextInt(5);
            CmsSubjectCategory cate = categoryMapper.selectById(d);

            if(cate!=null){
                subject.setCategoryName(cate.getName());
                subject.setCategoryId(Long.valueOf(d));
            }

            subject.setType(c);
            Integer b = r.nextInt(100);
           SysSchool school =  schools.get(a);
           if (school!=null){
               subject.setSchoolId(school.getId());
               subject.setSchoolName(school.getName());
           }else{
               SysSchool school1 =   schools.get(b);
               if (school1!=null){
                   subject.setSchoolId(school1.getId());
                   subject.setSchoolName(school1.getName());
               }
           }

            SysArea area =  areas.get(b);
            if (area!=null){
                subject.setAreaId(area.getId());
                subject.setAreaName(area.getName());
            }else{
                SysArea area1 =    areas.get(a);
                if (area1!=null){
                    subject.setAreaId(area1.getId());
                    subject.setAreaName(area1.getName());
                }
            }
              subjectMapper.updateById(subject);
        }
        return new CommonResult().success();
    }

}
