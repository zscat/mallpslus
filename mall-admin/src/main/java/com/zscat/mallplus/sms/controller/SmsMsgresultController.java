package com.zscat.mallplus.sms.controller;

import com.zscat.mallplus.sms.entity.SmsMsgresult;
import com.zscat.mallplus.sms.service.ISmsMsgresultService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zscat.mallplus.annotation.SysLog;
import com.zscat.mallplus.utils.CommonResult;
import com.zscat.mallplus.utils.ValidatorUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
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
@Api(tags = "SmsMsgresultController", description = "管理")
@RequestMapping("/sms/msgresult")
public class SmsMsgresultController {
    @Resource
    private ISmsMsgresultService ISmsMsgresultService;

    @SysLog(MODULE = "sms", REMARK = "查询sms_msgresult表")
    @ApiOperation("查询sms_msgresult表")
    @GetMapping(value = "/list")
    @PreAuthorize("hasAuthority('sms:SmsMsgresult:read')")
    public Object getSmsMsgresultByPage(SmsMsgresult entity,
                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize
    ) {
        try {
            return new CommonResult().success(ISmsMsgresultService.page(new Page<SmsMsgresult>(pageNum, pageSize), new QueryWrapper<>(entity)));
        } catch (Exception e) {
            log.error("分页获取sms_msgresult列表：%s", e.getMessage(), e);
        }
        return new CommonResult().failed();
    }

    @SysLog(MODULE = "sms", REMARK = "保存sms_msgresult表")
    @ApiOperation("保存sms_msgresult表")
    @PostMapping(value = "/create")
    @PreAuthorize("hasAuthority('sms:SmsMsgresult:create')")
    public Object saveMsgresult(@RequestBody SmsMsgresult entity) {
        try {
            if (ISmsMsgresultService.save(entity)) {
                return new CommonResult().success();
            }
        } catch (Exception e) {
            log.error("保存sms_msgresult表：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }

    @SysLog(MODULE = "sms", REMARK = "更新sms_msgresult")
    @ApiOperation("更新sms_msgresult")
    @PostMapping(value = "/update/{id}")
    @PreAuthorize("hasAuthority('sms:SmsMsgresult:update')")
    public Object updateMsgresult(@RequestBody SmsMsgresult entity) {
        try {
            if (ISmsMsgresultService.updateById(entity)) {
                return new CommonResult().success();
            }
        } catch (Exception e) {
            log.error("更新：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }
    @SysLog(MODULE = "sms", REMARK = "删除sms_msgresult数据")
    @ApiOperation("删除数据")
    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasAuthority('sms:SmsMsgresult:delete')")
    public Object deleteRole(@ApiParam("_id") @PathVariable Long id) {
        try {
            if (ValidatorUtils.empty(id)) {
                return new CommonResult().paramFailed("SmsMsgresult_id");
            }
            if (ISmsMsgresultService.removeById(id)) {
                return new CommonResult().success();
            }
        } catch (Exception e) {
            log.error("删除数据：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }
        return new CommonResult().failed();
    }

    @SysLog(MODULE = "sms", REMARK = "根据ID查询sms_msgresult")
    @ApiOperation("根据ID查询sms_msgresult")
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('sms:SmsMsgresult:read')")
    public Object getRoleById(@ApiParam("_id") @PathVariable Long id) {
        try {
            if (ValidatorUtils.empty(id)) {
                return new CommonResult().paramFailed("SmsMsgresult_id");
            }
            SmsMsgresult smsMsgresult = ISmsMsgresultService.getById(id);
            return new CommonResult().success(smsMsgresult);
        } catch (Exception e) {
            log.error("sms_msgresult表明细：%s", e.getMessage(), e);
            return new CommonResult().failed();
        }

    }

    @ApiOperation(value = "批量删除SmsMsgresult表")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    @ResponseBody
    @SysLog(MODULE = "sms", REMARK = "批量删除SmsMsgresult表")
    @PreAuthorize("hasAuthority('sms:SmsMsgresult:delete')")
    public Object deleteBatch(@RequestParam("ids") List<Long> ids) {
        boolean count = ISmsMsgresultService.removeByIds(ids);
        if (count) {
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }


}
