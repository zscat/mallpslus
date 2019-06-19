package com.zscat.mallplus.single;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zscat.mallplus.annotation.IgnoreAuth;
import com.zscat.mallplus.annotation.SysLog;
import com.zscat.mallplus.exception.ApiMallPlusException;
import com.zscat.mallplus.oms.entity.OmsOrder;
import com.zscat.mallplus.oms.entity.OmsOrderItem;
import com.zscat.mallplus.oms.service.IOmsOrderItemService;
import com.zscat.mallplus.oms.service.IOmsOrderService;
import com.zscat.mallplus.oms.vo.ConfirmOrderResult;
import com.zscat.mallplus.oms.vo.GroupAndOrderVo;
import com.zscat.mallplus.oms.vo.OrderParam;
import com.zscat.mallplus.sms.service.ISmsGroupService;
import com.zscat.mallplus.ums.entity.UmsMember;
import com.zscat.mallplus.utils.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Auther: shenzhuan
 * @Date: 2019/4/2 15:02
 * @Description:
 */
@RestController
@Api(tags = "OmsController", description = "订单管理系统")
@RequestMapping("/api/single/oms")
public class SingeOmsController extends ApiBaseAction {

    @Resource
    private ISmsGroupService groupService;
    @Resource
    private IOmsOrderService orderService;
    @Resource
    private IOmsOrderItemService orderItemService;

    @IgnoreAuth
    @SysLog(MODULE = "oms", REMARK = "查询订单列表")
    @ApiOperation(value = "查询订单列表")
    @GetMapping(value = "/order/list")
    public Object orderList(OmsOrder order,
                            @RequestParam(value = "pageSize", required = false, defaultValue = "100") Integer pageSize,
                            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {

        IPage<OmsOrder> page = null;
        if (order.getStatus()==0){
            page = orderService.page(new Page<OmsOrder>(pageNum, pageSize), new QueryWrapper<OmsOrder>().eq("member_id",order.getMemberId())) ;
        }else {
            page = orderService.page(new Page<OmsOrder>(pageNum, pageSize), new QueryWrapper<>(order)) ;

        }
        for (OmsOrder omsOrder : page.getRecords()){
            List<OmsOrderItem> itemList = orderItemService.list(new QueryWrapper<OmsOrderItem>().eq("order_id",omsOrder.getId()));
            omsOrder.setOrderItemList(itemList);
        }
        return new CommonResult().success(page);
    }
    @ApiOperation("获取订单详情:订单信息、商品信息、操作记录")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public Object detail(@RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        OmsOrder orderDetailResult = null;
            orderDetailResult = orderService.getById(id);
            OmsOrderItem query = new OmsOrderItem();
            query.setOrderId(id);
            List<OmsOrderItem> orderItemList = orderItemService.list(new QueryWrapper<>(query));
            orderDetailResult.setOrderItemList(orderItemList);
        return new CommonResult().success(orderDetailResult);
    }

    /**
     * 提交订单
     *
     * @param orderParam
     * @return
     */
    @ApiOperation("商品详情预览订单")
    @SysLog(MODULE = "order", REMARK = "商品详情预览订单")
    @GetMapping(value = "/preOrder")
    public Object preOrder(GroupAndOrderVo orderParam) {
        UmsMember member = this.getCurrentMember();
        orderParam.setMemberId(member.getId());
        orderParam.setName(member.getNickname());
        return orderService.preSingelOrder(orderParam);
    }

    /**
     * 提交订单
     *
     * @param orderParam
     * @return
     */
    @ApiOperation("商品详情生成订单")
    @SysLog(MODULE = "order", REMARK = "商品详情生成订单")
    @PostMapping(value = "/bookOrder")
    public Object bookOrder(GroupAndOrderVo orderParam) {
        UmsMember member = this.getCurrentMember();
        return orderService.generateSingleOrder(orderParam, member);
    }

    @ResponseBody
    @GetMapping("/submitPreview")
    public Object submitPreview(OrderParam orderParam) {
        try {
            ConfirmOrderResult result = orderService.submitPreview(orderParam);
            return new CommonResult().success(result);
        } catch (ApiMallPlusException e) {
            return new CommonResult().failed(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 提交订单
     *
     * @param orderParam
     * @return
     */
    @ApiOperation("根据购物车信息生成订单")
    @RequestMapping(value = "/generateOrder")
    @ResponseBody
    public Object generateOrder(OrderParam orderParam) {
        return orderService.generateOrder(orderParam);
    }

}
