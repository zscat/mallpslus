package com.zscat.mallplus.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zscat.mallplus.ExportGoods;
import com.zscat.mallplus.ExportUser;
import com.zscat.mallplus.annotation.SysLog;
import com.zscat.mallplus.bo.HomeOrderData;
import com.zscat.mallplus.oms.entity.OmsOrder;
import com.zscat.mallplus.oms.service.IOmsOrderService;
import com.zscat.mallplus.pms.entity.PmsProduct;
import com.zscat.mallplus.pms.service.IPmsProductService;
import com.zscat.mallplus.ums.entity.UmsMember;
import com.zscat.mallplus.ums.service.IUmsMemberService;
import com.zscat.mallplus.util.DateUtils;
import com.zscat.mallplus.util.EasyPoiUtils;
import com.zscat.mallplus.utils.CommonResult;
import com.zscat.mallplus.utils.ValidatorUtils;
import com.zscat.mallplus.vo.OrderStatusCount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Auther: shenzhuan
 * @Date: 2019/3/27 18:57
 * @Description:
 */
@RestController
@Api(tags = "HomeController", description = "首页管理")
@RequestMapping("/home")
public class HomeController extends BaseController {

    @Resource
    private IOmsOrderService orderService;
    @Resource
    private IPmsProductService productService;
    @Resource
    private IUmsMemberService memberService;

    /**
     * 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
     *
     * @return
     */
    @ApiOperation("首页订单统计")
    @SysLog(MODULE = "home", REMARK = "首页订单统计")
    @RequestMapping(value = "/orderStatic", method = RequestMethod.GET)
    public Object orderStatic() throws Exception {
        HomeOrderData data = new HomeOrderData();
        List<OmsOrder> orderList = orderService.list(new QueryWrapper<>());
        int nowOrderCount = 0; // 今日订单
        BigDecimal nowOrderPay = new BigDecimal(0); //今日销售总额

        int yesOrderCount = 0; // 昨日订单
        BigDecimal yesOrderPay = new BigDecimal(0); //日销售总额

        int qiOrderCount = 0; // 7日订单
        BigDecimal qiOrderPay = new BigDecimal(0); //7日销售总额

        int monthOrderCount = 0; // 本月订单
        BigDecimal monthOrderPay = new BigDecimal(0); //本月销售总额

        int weekOrderCount = 0; // 本月订单
        BigDecimal weekOrderPay = new BigDecimal(0); //本月销售总额

        int status0 = 0;
        int status1 = 0;
        int status2 = 0;
        int status3 = 0;
        int status4 = 0;
        int status5 = 0;
        OrderStatusCount count = new OrderStatusCount();

        for (OmsOrder order : orderList) {
            if (DateUtils.format(order.getCreateTime()).equals(DateUtils.format(new Date()))
                    && (order.getStatus() == 1 || order.getStatus() == 2 || order.getStatus() == 3)) {
                nowOrderCount++;
                nowOrderPay = nowOrderPay.add(order.getPayAmount());
            }
            if (DateUtils.format(order.getCreateTime()).equals(DateUtils.addDay(new Date(), -1))
                    && (order.getStatus() == 1 || order.getStatus() == 2 || order.getStatus() == 3)) {
                yesOrderCount++;
                yesOrderPay = yesOrderPay.add(order.getPayAmount());
            }
            if (DateUtils.calculateDaysNew(order.getCreateTime(), new Date()) >= 7
                    && (order.getStatus() == 1 || order.getStatus() == 2 || order.getStatus() == 3)) {
                qiOrderCount++;
                qiOrderPay = qiOrderPay.add(order.getPayAmount());
            }
            if (order.getCreateTime().getTime() >= DateUtils.geFirstDayDateByMonth().getTime()
                    && (order.getStatus() == 1 || order.getStatus() == 2 || order.getStatus() == 3)) {
                monthOrderCount++;
                monthOrderPay = monthOrderPay.add(order.getPayAmount());
            }
            if (order.getCreateTime().getTime() >= DateUtils.getFirstDayOfWeek().getTime()
                    && (order.getStatus() == 1 || order.getStatus() == 2 || order.getStatus() == 3)) {
                weekOrderCount++;
                weekOrderPay = weekOrderPay.add(order.getPayAmount());
            }
            if (order.getStatus() == 0) {
                status0++;
            }
            if (order.getStatus() == 1) {
                status1++;
            }
            if (order.getStatus() == 2) {
                status2++;
            }
            if (order.getStatus() == 3) {
                status3++;
            }
            if (order.getStatus() == 4) {
                status4++;
            }
            if (order.getStatus() == 5) {
                status5++;
            }

        }
        count.setStatus0(status0);
        count.setStatus1(status1);
        count.setStatus2(status2);
        count.setStatus3(status3);
        count.setStatus4(status4);
        count.setStatus5(status5);

        data.setNowOrderCount(nowOrderCount);
        data.setNowOrderPay(nowOrderPay);
        data.setYesOrderCount(yesOrderCount);
        data.setYesOrderPay(yesOrderPay);
        data.setQiOrderCount(qiOrderCount);
        data.setQiOrderPay(qiOrderPay);
        data.setOrderStatusCount(count);
        data.setMonthOrderCount(monthOrderCount);
        data.setMonthOrderPay(monthOrderPay);
        data.setWeekOrderCount(weekOrderCount);
        data.setWeekOrderPay(weekOrderPay);
        return new CommonResult().success(data);
    }

    @ApiOperation("首页商品统计")
    @SysLog(MODULE = "home", REMARK = "首页商品统计")
    @RequestMapping(value = "/goodsStatic", method = RequestMethod.GET)
    public Object goodsStatic() throws Exception {
        List<PmsProduct> goodsList = productService.list(new QueryWrapper<>(new PmsProduct()));
        int onCount = 0;
        int offCount = 0;
        int nowCount = 0;
        for (PmsProduct goods : goodsList) {
            if (goods.getPublishStatus() == 1) { // 上架状态：0->下架；1->上架
                onCount++;
            }
            if (goods.getPublishStatus() == 0) { // 上架状态：0->下架；1->上架
                offCount++;
            }
            if (DateUtils.format(goods.getCreateTime()).equals(DateUtils.format(new Date()))) {
                nowCount++;
            }
        }
        Map<String, Object> map = new HashMap();
        map.put("onCount", onCount);
        map.put("offCount", offCount);
        map.put("nowCount", nowCount);
        map.put("allCount", goodsList.size());
        return new CommonResult().success(map);
    }

    @ApiOperation("首页会员统计")
    @SysLog(MODULE = "home", REMARK = "首页会员统计")
    @RequestMapping(value = "/userStatic", method = RequestMethod.GET)
    public Object userStatic() throws Exception {
        List<UmsMember> memberList = memberService.list(new QueryWrapper<>());
        int nowCount = 0;
        int yesUserCount = 0; // 昨日
        int qiUserCount = 0; // 当日
        for (UmsMember member : memberList) {
            if (DateUtils.format(member.getCreateTime()).equals(DateUtils.addDay(new Date(), -1))) {
                yesUserCount++;
            }
            if (member.getCreateTime().getTime() >= DateUtils.geFirstDayDateByMonth().getTime()) {
                qiUserCount++;
            }
            if (DateUtils.format(member.getCreateTime()).equals(DateUtils.format(new Date()))) {
                nowCount++;
            }
        }
        Map<String, Object> map = new HashMap();
        map.put("qiUserCount", qiUserCount);
        map.put("yesUserCount", yesUserCount);
        map.put("nowCount", nowCount);
        map.put("allCount", memberList.size());
        return new CommonResult().success(map);
    }


    /**
     * 导出：http://localhost:8080/home/export/users
     导入：http://localhost:8080/demo/import/users (用postman或者curl测试吧)
     http://localhost:8080/home/import/goods
     * @param response
     */
    @RequestMapping(value = "/export/users", method = RequestMethod.GET)
    public void exportUsers(HttpServletResponse response) {
        List<ExportUser> userList = getUserList();
        EasyPoiUtils.exportExcel(getUserList(), "用户列表", "用户报表", ExportUser.class, "用户明细报表.xls", response);
    }
    @RequestMapping("/import/users")
    @ResponseBody
    public List<ExportUser> importUsers(@RequestParam MultipartFile file) {
        List<ExportUser> d = EasyPoiUtils.importExcel(file, ExportUser.class);
        return EasyPoiUtils.importExcel(file, ExportUser.class);
    }
    @RequestMapping("/import/goods")
    @ResponseBody
    public void importgoods(@RequestParam MultipartFile file) {
        List<ExportGoods> list = EasyPoiUtils.importExcel(file, ExportGoods.class);
        for (ExportGoods gg : list){
            createG(gg);
        }
    }
    void createG(ExportGoods gg){
        PmsProduct g = new PmsProduct();
        g.setName(gg.getUsername());
        g.setSubTitle(gg.getUsername());
        g.setDescription(gg.getDetail1());
        g.setDetailHtml(gg.getDetail());
        g.setDetailMobileHtml(gg.getDetail());
        g.setDetailTitle(gg.getUsername());
        g.setDetailDesc(gg.getUsername());

        g.setPic(gg.getImg());
        g.setAlbumPics(gg.getImg());
        if (ValidatorUtils.notEmpty(gg.getPrice())){
            g.setPrice(new BigDecimal(gg.getPrice().substring(1)));
        }
        if (ValidatorUtils.notEmpty(gg.getOriginprice())){
            g.setOriginalPrice(new BigDecimal(gg.getOriginprice().substring(1)));
        }

        g.setSale(0);
        g.setStock(0);
        g.setLowStock(0);
        g.setGiftPoint(0);
        g.setGiftGrowth(0);
        g.setPromotionType(0);
        g.setVerifyStatus(1);
        g.setProductSn("X"+System.currentTimeMillis());
        g.setQsType(1);
        g.setNewStatus(1);
        g.setCreateTime(new Date());

        g.setBrandId(64L);
        g.setBrandName("红蜻蜓");
        g.setProductCategoryId(61L);
        g.setProductCategoryName("品牌男鞋");
        g.setProductAttributeCategoryId(11L);
        productService.save(g);
    }
    private List<ExportUser> getUserList() {
        List<ExportUser> userList = new ArrayList<>();
        userList.add(new ExportUser("tom", new Date()));
        userList.add(new ExportUser("jack", new Date()));
        userList.add(new ExportUser("123", new Date()));
        return userList;
    }

}
