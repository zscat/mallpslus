package com.zscat.mallplus.sms.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.zscat.mallplus.sms.entity.SmsPhonenum;
import com.zscat.mallplus.sms.service.ISmsPhonenumService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zscat.mallplus.annotation.SysLog;
import com.zscat.mallplus.sms.vo.PhoneNumVo;
import com.zscat.mallplus.sys.controller.BaseController;
import com.zscat.mallplus.util.JsonUtil;
import com.zscat.mallplus.utils.CommonResult;
import com.zscat.mallplus.utils.ValidatorUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author cxh
 * @since ${date}
 */
@Slf4j
@RestController
@Api(tags = "SmsPhonenumController", description = "管理")
@RequestMapping("/sms/phonenum")
public class SmsPhonenumController extends BaseController {
    @Resource
    private ISmsPhonenumService ISmsPhonenumService;

    @SysLog(MODULE = "sms", REMARK = "查询sms_phonenum表")
    @ApiOperation("查询sms_phonenum表")
    @GetMapping(value = "/list")
    @PreAuthorize("hasAuthority('sms:SmsPhonenum:read')")
    public Object getSmsPhonenumByPage(SmsPhonenum entity,
                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize
    ) {
        try {
            return new CommonResult().success(ISmsPhonenumService.page(new Page<SmsPhonenum>(pageNum, pageSize), new QueryWrapper<>(entity)));
        } catch (Exception e) {
            log.error("分页获取sms_phonenum列表：%s", e.getMessage(), e);
        }
        return new CommonResult().failed();
    }

    @SysLog(MODULE = "sms", REMARK = "保存sms_phonenum表")
    @ApiOperation("保存sms_phonenum表")
    @PostMapping(value = "/create")
    @PreAuthorize("hasAuthority('sms:SmsPhonenum:create')")
    public Object savePhonenum(@RequestBody SmsPhonenum entity) {
        try {
            if (ISmsPhonenumService.save(entity)) {
                return new CommonResult().success();
            }
        } catch (Exception e) {
            log.error("保存sms_phonenum表：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }

    @SysLog(MODULE = "sms", REMARK = "保存sms_phonenum表")
    @ApiOperation("保存sms_phonenum表")
    @RequestMapping(value = "/createList", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('sms:SmsPhonenum:create')")
    public Object savePhonenumList(@RequestParam(value = "tableData")String tableData) {
        try {
//          String tableHeader = request.getParameter("tableHeader");
            List<PhoneNumVo> vos= JsonUtil.jsonToList(tableData,PhoneNumVo.class);
            List<SmsPhonenum> list = new ArrayList<SmsPhonenum>();
            for (int i = 0 ; i < vos.size();i++){

                SmsPhonenum smsPhonenum = new SmsPhonenum();
                smsPhonenum.setPhonenum(vos.get(i).getPhoneNum());
                smsPhonenum.setUserid(getCurrentUser().getId());
                //判断是否有值，如果有，则不添加
                QueryWrapper<SmsPhonenum> q = new QueryWrapper<SmsPhonenum>();

               if(!(ISmsPhonenumService.list(new QueryWrapper<>(smsPhonenum)).size() > 0)){
                   list.add(smsPhonenum);
               }

            }
            if(list.size()==0){
                return new CommonResult().successNull();
            }
            if (ISmsPhonenumService.saveOrUpdateBatch(list)) {
                return new CommonResult().success();
            }
        } catch (Exception e) {
            log.error("保存sms_phonenum表：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }

    @SysLog(MODULE = "sms", REMARK = "更新sms_phonenum")
    @ApiOperation("更新sms_phonenum")
    @PostMapping(value = "/update/{id}")
    @PreAuthorize("hasAuthority('sms:SmsPhonenum:update')")
    public Object updatePhonenum(@RequestBody SmsPhonenum entity) {
        try {
            if (ISmsPhonenumService.updateById(entity)) {
                return new CommonResult().success();
            }
        } catch (Exception e) {
            log.error("更新：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }
    @SysLog(MODULE = "sms", REMARK = "删除sms_phonenum数据")
    @ApiOperation("删除数据")
    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasAuthority('sms:SmsPhonenum:delete')")
    public Object deleteRole(@ApiParam("_id") @PathVariable Long id) {
        try {
            if (ValidatorUtils.empty(id)) {
                return new CommonResult().paramFailed("SmsPhonenum_id");
            }
            if (ISmsPhonenumService.removeById(id)) {
                return new CommonResult().success();
            }
        } catch (Exception e) {
            log.error("删除数据：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }

    @SysLog(MODULE = "sms", REMARK = "根据ID查询sms_phonenum")
    @ApiOperation("根据ID查询sms_phonenum")
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('sms:SmsPhonenum:read')")
    public Object getRoleById(@ApiParam("_id") @PathVariable Long id) {
        try {
            if (ValidatorUtils.empty(id)) {
                return new CommonResult().paramFailed("SmsPhonenum_id");
            }
            SmsPhonenum smsPhonenum = ISmsPhonenumService.getById(id);
            return new CommonResult().success(smsPhonenum);
        } catch (Exception e) {
            log.error("sms_phonenum表明细：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }

    }

    @ApiOperation(value = "批量删除SmsPhonenum表")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    @ResponseBody
    @SysLog(MODULE = "sms", REMARK = "批量删除SmsPhonenum表")
    @PreAuthorize("hasAuthority('sms:SmsPhonenum:delete')")
    public Object deleteBatch(@RequestParam("ids") List<Long> ids) {
        boolean count = ISmsPhonenumService.removeByIds(ids);
        if (count) {
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }


}
