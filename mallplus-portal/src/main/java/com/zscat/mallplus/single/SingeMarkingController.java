package com.zscat.mallplus.single;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zscat.mallplus.annotation.IgnoreAuth;
import com.zscat.mallplus.annotation.SysLog;
import com.zscat.mallplus.cms.service.ISysAreaService;
import com.zscat.mallplus.cms.service.ISysSchoolService;
import com.zscat.mallplus.sms.entity.SmsBasicGifts;
import com.zscat.mallplus.sms.entity.SmsBasicMarking;
import com.zscat.mallplus.sms.service.ISmsBasicGiftsService;
import com.zscat.mallplus.sms.service.ISmsBasicMarkingService;
import com.zscat.mallplus.sys.entity.SysArea;
import com.zscat.mallplus.sys.entity.SysSchool;
import com.zscat.mallplus.ums.entity.UmsEmployInfo;
import com.zscat.mallplus.ums.entity.UmsMember;
import com.zscat.mallplus.ums.mapper.UmsEmployInfoMapper;
import com.zscat.mallplus.ums.mapper.UmsRewardLogMapper;
import com.zscat.mallplus.ums.service.IUmsMemberMemberTagRelationService;
import com.zscat.mallplus.ums.service.IUmsMemberService;
import com.zscat.mallplus.ums.service.RedisService;
import com.zscat.mallplus.util.UserUtils;
import com.zscat.mallplus.utils.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * @Auther: shenzhuan
 * @Date: 2019/4/2 15:02
 * @Description:
 */
@RestController
@Api(tags = "SingeMarkingController", description = "营销管理")
@RequestMapping("/api/single/sms")
public class SingeMarkingController extends ApiBaseAction {

    @Resource
    private ISmsBasicGiftsService basicGiftsService;
    @Resource
    private ISmsBasicMarkingService basicMarkingService;
    @Resource
    private RedisService redisService;
    @ApiOperation("获取单个商品得优惠详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public Object detail(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        List<SmsBasicMarking> list1 = basicMarkingService.matchGoodsMk(id);
        List<SmsBasicGifts> listg = basicGiftsService.matchGoodsMk(id);
        return new CommonResult().success(1);
    }


}
