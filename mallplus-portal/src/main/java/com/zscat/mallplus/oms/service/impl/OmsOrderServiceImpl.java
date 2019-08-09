package com.zscat.mallplus.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zscat.mallplus.config.WxAppletProperties;
import com.zscat.mallplus.enums.OrderStatus;
import com.zscat.mallplus.exception.ApiMallPlusException;
import com.zscat.mallplus.oms.entity.OmsCartItem;
import com.zscat.mallplus.oms.entity.OmsOrder;
import com.zscat.mallplus.oms.entity.OmsOrderItem;
import com.zscat.mallplus.oms.entity.OmsOrderSetting;
import com.zscat.mallplus.oms.mapper.OmsCartItemMapper;
import com.zscat.mallplus.oms.mapper.OmsOrderMapper;
import com.zscat.mallplus.oms.mapper.OmsOrderSettingMapper;
import com.zscat.mallplus.oms.service.IOmsCartItemService;
import com.zscat.mallplus.oms.service.IOmsOrderItemService;
import com.zscat.mallplus.oms.service.IOmsOrderService;
import com.zscat.mallplus.oms.vo.*;
import com.zscat.mallplus.pms.entity.PmsGifts;
import com.zscat.mallplus.pms.entity.PmsProduct;
import com.zscat.mallplus.pms.entity.PmsSkuStock;
import com.zscat.mallplus.pms.mapper.PmsSkuStockMapper;
import com.zscat.mallplus.pms.service.IPmsGiftsService;
import com.zscat.mallplus.pms.service.IPmsProductService;
import com.zscat.mallplus.sms.entity.*;
import com.zscat.mallplus.sms.mapper.SmsGroupMapper;
import com.zscat.mallplus.sms.mapper.SmsGroupMemberMapper;
import com.zscat.mallplus.sms.service.ISmsCouponHistoryService;
import com.zscat.mallplus.sms.service.ISmsCouponService;
import com.zscat.mallplus.sms.service.ISmsGroupMemberService;
import com.zscat.mallplus.sms.service.ISmsGroupService;
import com.zscat.mallplus.sms.vo.SmsCouponHistoryDetail;
import com.zscat.mallplus.ums.entity.UmsIntegrationConsumeSetting;
import com.zscat.mallplus.ums.entity.UmsMember;
import com.zscat.mallplus.ums.entity.UmsMemberReceiveAddress;
import com.zscat.mallplus.ums.mapper.UmsIntegrationConsumeSettingMapper;
import com.zscat.mallplus.ums.service.IUmsMemberReceiveAddressService;
import com.zscat.mallplus.ums.service.IUmsMemberService;
import com.zscat.mallplus.ums.service.RedisService;
import com.zscat.mallplus.util.DateUtils;
import com.zscat.mallplus.util.UserUtils;
import com.zscat.mallplus.util.applet.TemplateData;
import com.zscat.mallplus.util.applet.WX_TemplateMsgUtil;
import com.zscat.mallplus.utils.CommonResult;
import com.zscat.mallplus.utils.ValidatorUtils;
import com.zscat.mallplus.vo.CartParam;
import com.zscat.mallplus.vo.Rediskey;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-04-17
 */
@Service
@Slf4j
public class OmsOrderServiceImpl extends ServiceImpl<OmsOrderMapper, OmsOrder> implements IOmsOrderService {

    @Resource
    private RedisService redisService;
    @Value("${redis.key.prefix.orderId}")
    private String REDIS_KEY_PREFIX_ORDER_ID;
    @Resource
    private IPmsProductService productService;
    @Resource
    private IUmsMemberReceiveAddressService addressService;

    @Autowired
    private WxAppletProperties wxAppletProperties;

    @Resource
    private WechatApiService wechatApiService;
    @Resource
    private ISmsGroupService groupService;
    @Resource
    private ISmsGroupMemberService groupMemberService;
    @Resource
    private IOmsCartItemService cartItemService;

    @Resource
    private ISmsCouponService couponService;
    @Resource
    private UmsIntegrationConsumeSettingMapper integrationConsumeSettingMapper;
    @Resource
    private PmsSkuStockMapper skuStockMapper;
    @Resource
    private ISmsCouponHistoryService couponHistoryService;
    @Resource
    private IOmsOrderService orderService;
    @Resource
    private IOmsOrderItemService orderItemService;
    @Resource
    private OmsOrderMapper orderMapper;
    @Resource
    private SmsGroupMemberMapper groupMemberMapper;
    @Resource
    private IUmsMemberService memberService;
    @Resource
    private OmsOrderSettingMapper orderSettingMapper;
    @Resource
    private OmsCartItemMapper cartItemMapper;
    @Resource
    private SmsGroupMapper groupMapper;
    @Resource
    private IPmsGiftsService giftsService;

    @Override
    public int payOrder(TbThanks tbThanks) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        tbThanks.setTime(time);
        tbThanks.setDate(new Date());
        /*TbMember tbMember=tbMemberMapper.selectByPrimaryKey(Long.valueOf(tbThanks.getUserId()));
        if(tbMember!=null){
            tbThanks.setUsername(tbMember.getUsername());
        }
        if(tbThanksMapper.insert(tbThanks)!=1){
            throw new XmallException("保存捐赠支付数据失败");
        }*/

        //设置订单为已付款
        OmsOrder tbOrder = orderMapper.selectById(tbThanks.getOrderId());
        if (tbOrder == null) {
            throw new ApiMallPlusException("订单不存在");
        }
        tbOrder.setStatus(2);
        tbOrder.setPayType(tbThanks.getPayType());
        tbOrder.setPaymentTime(new Date());
        tbOrder.setModifyTime(new Date());
        if (orderMapper.updateById(tbOrder) != 1) {
            throw new ApiMallPlusException("更新订单失败");
        }
        //恢复所有下单商品的锁定库存，扣减真实库存
        OmsOrderItem queryO = new OmsOrderItem();
        queryO.setOrderId(tbThanks.getOrderId());
        List<OmsOrderItem> list = orderItemService.list(new QueryWrapper<>(queryO));

        int count = orderMapper.updateSkuStock(list);
        //发送通知确认邮件
        String tokenName = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();

        // emailUtil.sendEmailDealThank(EMAIL_SENDER,"【mallcloud商城】支付待审核处理",tokenName,token,tbThanks);
        return count;
    }

    @Override
    public void sendDelayMessageCancelOrder(Long orderId) {
        //获取订单超时时间
        OmsOrderSetting orderSetting = orderSettingMapper.selectById(1L);
        long delayTimes = orderSetting.getNormalOrderOvertime() * 60 * 1000;
        //发送延迟消息
        //  cancelOrderSender.sendMessage(orderId, delayTimes);
    }

    /**
     * @return
     */
    @Override
    public ConfirmOrderResult submitPreview(OrderParam orderParam) {

        String type = orderParam.getType();

        UmsMember currentMember = UserUtils.getCurrentMember();
        List<OmsCartItem> list = new ArrayList<>();
        if ("3".equals(type)) { // 1 商品详情 2 勾选购物车 3全部购物车的商品
           // cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(), null);
        }
        if ("1".equals(type)) {

            String cartId = orderParam.getCartId();
            if (org.apache.commons.lang.StringUtils.isBlank(cartId)) {
                throw new ApiMallPlusException("参数为空");
            }
            OmsCartItem omsCartItem = cartItemService.selectById(Long.valueOf(cartId));
            if (omsCartItem==null){
                return  null;
            }
            list.add(omsCartItem);

        } else if ("2".equals(type)) {
            String cart_id_list1 = orderParam.getCartIds();
            if (org.apache.commons.lang.StringUtils.isBlank(cart_id_list1)) {
                throw new ApiMallPlusException("参数为空");
            }
            String[] ids1 = cart_id_list1.split(",");
            List<Long> resultList = new ArrayList<>(ids1.length);
            for (String s : ids1) {
                resultList.add(Long.valueOf(s));
            }
            list = cartItemService.list(currentMember.getId(), resultList);
        }
        if (list == null && list.size() < 1) {
            throw new ApiMallPlusException("订单已提交");
        }
        ConfirmOrderResult result = new ConfirmOrderResult();
        //获取购物车信息

        result.setCartPromotionItemList(list);
        //获取用户收货地址列表
        UmsMemberReceiveAddress queryU = new UmsMemberReceiveAddress();
        queryU.setMemberId(currentMember.getId());
        List<UmsMemberReceiveAddress> memberReceiveAddressList = addressService.list(new QueryWrapper<>(queryU));
        result.setMemberReceiveAddressList(memberReceiveAddressList);
        UmsMemberReceiveAddress address = addressService.getDefaultItem();
        //获取用户可用优惠券列表
        List<SmsCouponHistoryDetail> couponHistoryDetailList = couponService.listCart(list, 1);
        result.setCouponHistoryDetailList(couponHistoryDetailList);
        //获取用户积分
        result.setMemberIntegration(currentMember.getIntegration());
        //获取积分使用规则
        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectById(1L);
        result.setIntegrationConsumeSetting(integrationConsumeSetting);
        //计算总金额、活动优惠、应付金额
        if (list!=null && list.size()>0){
            ConfirmOrderResult.CalcAmount calcAmount = calcCartAmount(list);
            result.setCalcAmount(calcAmount);
            result.setAddress(address);
            return result;
        }
        return null;

    }

    @Override
    public ConfirmOrderResult generateConfirmOrder() {
        ConfirmOrderResult result = new ConfirmOrderResult();
        //获取购物车信息
        UmsMember currentMember = UserUtils.getCurrentMember();
        List<OmsCartItem> cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(), null);
        result.setCartPromotionItemList(cartPromotionItemList);
        //获取用户收货地址列表
        UmsMemberReceiveAddress queryU = new UmsMemberReceiveAddress();
        queryU.setMemberId(currentMember.getId());
        List<UmsMemberReceiveAddress> memberReceiveAddressList = addressService.list(new QueryWrapper<>(queryU));
        result.setMemberReceiveAddressList(memberReceiveAddressList);
        //获取用户可用优惠券列表
        List<SmsCouponHistoryDetail> couponHistoryDetailList = couponService.listCart(cartPromotionItemList, 1);
        result.setCouponHistoryDetailList(couponHistoryDetailList);
        //获取用户积分
        result.setMemberIntegration(currentMember.getIntegration());
        //获取积分使用规则
        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectById(1L);
        result.setIntegrationConsumeSetting(integrationConsumeSetting);
        //计算总金额、活动优惠、应付金额
        ConfirmOrderResult.CalcAmount calcAmount = calcCartAmount(cartPromotionItemList);
        result.setCalcAmount(calcAmount);
        return result;
    }

    @Override
    public CommonResult generateOrder(OrderParam orderParam) {

        String type = orderParam.getType();
        UmsMember currentMember = UserUtils.getCurrentMember();
        List<OmsCartItem> cartPromotionItemList = new ArrayList<>();
        if ("3".equals(type)) { // 1 商品详情 2 勾选购物车 3全部购物车的商品
            cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(), null);
        }
        if ("1".equals(type)) {
            Long cartId = Long.valueOf(orderParam.getCartId());
            OmsCartItem omsCartItem = cartItemService.selectById(cartId);
            List<OmsCartItem> list = new ArrayList<>();
            if (omsCartItem != null) {
                list.add(omsCartItem);
            } else {
                throw new ApiMallPlusException("订单已提交");
            }
            if (!CollectionUtils.isEmpty(list)) {
                cartPromotionItemList = cartItemService.calcCartPromotion(list);
            }
        } else if ("2".equals(type)) {
            String cart_id_list1 = orderParam.getCartIds();
            String[] ids1 = cart_id_list1.split(",");
            List<Long> resultList = new ArrayList<>(ids1.length);
            for (String s : ids1) {
                resultList.add(Long.valueOf(s));
            }

            cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(), resultList);
        }


        List<OmsOrderItem> orderItemList = new ArrayList<>();
        //获取购物车及优惠信息
        String name = "";

        for (OmsCartItem cartPromotionItem : cartPromotionItemList) {
            PmsProduct goods = productService.getById(cartPromotionItem.getProductId());
            if (!ValidatorUtils.empty(cartPromotionItem.getProductSkuId()) && cartPromotionItem.getProductSkuId() > 0) {
                checkGoods(goods, false, cartPromotionItem.getQuantity());
                PmsSkuStock skuStock = skuStockMapper.selectById(cartPromotionItem.getProductSkuId());
                checkSkuGoods(skuStock, cartPromotionItem.getQuantity());
            } else {
                checkGoods(goods, true, cartPromotionItem.getQuantity());
            }
            //生成下单商品信息
            OmsOrderItem orderItem = createOrderItem(cartPromotionItem);
           /* orderItem.setPromotionAmount(cartPromotionItem.getReduceAmount());
            orderItem.setPromotionName(cartPromotionItem.getPromotionMessage());
            orderItem.setGiftIntegration(cartPromotionItem.getIntegration());
            orderItem.setGiftGrowth(cartPromotionItem.getGrowth());*/
            orderItemList.add(orderItem);
            name = cartPromotionItem.getProductName();
        }

        UmsMemberReceiveAddress address = addressService.getById(orderParam.getAddressId());
        //根据商品合计、运费、活动优惠、优惠券、积分计算应付金额
        OmsOrder order = createOrderObj(orderParam,currentMember,orderItemList,address);
        // TODO: 2018/9/3 bill_*,delivery_*
        //插入order表和order_item表
        orderService.save(order);
        for (OmsOrderItem orderItem : orderItemList) {
            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getOrderSn());
        }
        orderItemService.saveBatch(orderItemList);
        //如使用优惠券更新优惠券使用状态
        if (orderParam.getCouponId() != null) {
            updateCouponStatus(orderParam.getCouponId(), currentMember.getId(), 1);
        }
        //如使用积分需要扣除积分
        if (orderParam.getUseIntegration() != null) {
            order.setUseIntegration(orderParam.getUseIntegration());
            memberService.updateIntegration(currentMember.getId(), currentMember.getIntegration() - orderParam.getUseIntegration());
        }
        //删除购物车中的下单商品
        deleteCartItemList(cartPromotionItemList, currentMember);
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItemList", orderItemList);

        String platform = orderParam.getPlatform();
        if ("1".equals(platform)) {
            push(currentMember, order, orderParam.getPage(), orderParam.getFormId(), name);
        }
        return new CommonResult().success("下单成功", result);
    }

    @Override
    public CommonResult acceptGroup(OrderParam orderParam) {


        UmsMember currentMember = UserUtils.getCurrentMember();
        List<OmsCartItem> list = new ArrayList<>();
        if (ValidatorUtils.empty(orderParam.getTotal())) {
            orderParam.setTotal(1);
        }
        OmsCartItem cartItem = new OmsCartItem();
        PmsProduct pmsProduct = productService.getById(orderParam.getGoodsId());
        createCartObj(orderParam, list, cartItem, pmsProduct);


        List<OmsOrderItem> orderItemList = new ArrayList<>();
        //获取购物车及优惠信息
        String name = "";

        for (OmsCartItem cartPromotionItem : list) {
            PmsProduct goods = productService.getById(cartPromotionItem.getProductId());
            if (!ValidatorUtils.empty(cartPromotionItem.getProductSkuId()) && cartPromotionItem.getProductSkuId() > 0) {
                checkGoods(goods, false, cartPromotionItem.getQuantity());
                PmsSkuStock skuStock = skuStockMapper.selectById(cartPromotionItem.getProductSkuId());
                checkSkuGoods(skuStock, cartPromotionItem.getQuantity());
            } else {
                checkGoods(goods, true, cartPromotionItem.getQuantity());
            }
            //生成下单商品信息
            OmsOrderItem orderItem = createOrderItem(cartPromotionItem);
            orderItemList.add(orderItem);
        }

        //进行库存锁定
        lockStock(list);
        //根据商品合计、运费、活动优惠、优惠券、积分计算应付金额
        UmsMemberReceiveAddress address = addressService.getById(orderParam.getAddressId());

        OmsOrder order = createOrderObj(orderParam, currentMember, orderItemList, address);

        order.setMemberId(orderParam.getMemberId());
        SmsGroup group = groupMapper.getByGoodsId(orderParam.getGoodsId());
        Date endTime = DateUtils.convertStringToDate(DateUtils.addHours(group.getEndTime(), group.getHours()), "yyyy-MM-dd HH:mm:ss");

        // TODO: 2018/9/3 bill_*,delivery_*
        //插入order表和order_item表
        orderService.save(order);
        Long nowT = System.currentTimeMillis();
        if (nowT > group.getStartTime().getTime() && nowT < endTime.getTime()) {
            SmsGroupMember groupMember = new SmsGroupMember();

            if(orderParam.getGroupType()==1){
                groupMember.setMainId(orderParam.getMemberId());
                groupMember.setGoodsId(orderParam.getGoodsId());
                SmsGroupMember    exist = groupMemberMapper.selectOne(new QueryWrapper<>(groupMember));
                if (exist!=null){
                    return new CommonResult().failed("你已经参加过此活动");
                }
                groupMember.setName(currentMember.getIcon());
                groupMember.setStatus(2);
                groupMember.setOrderId(order.getId()+"");
                groupMember.setMainId(orderParam.getMemberId());
                groupMember.setCreateTime(new Date());
                groupMember.setGroupId(group.getId());

                groupMember.setMemberId(orderParam.getMemberId()+"");
                groupMember.setExipreTime(System.currentTimeMillis()+(group.getHours()*60*60*60));
                groupMemberMapper.insert(groupMember);
            }else{
                groupMember = groupMemberMapper.selectById(orderParam.getMgId());
               String []mids = groupMember.getMemberId().split(",");
              for (int i=0;i<mids.length;i++){
                  if (orderParam.getMemberId().toString().equals(mids[i])){
                      return new CommonResult().failed("你已经参加过此活动");
                  }
              }

                groupMember.setName(groupMember.getName()+","+currentMember.getIcon());
                groupMember.setOrderId(groupMember.getOrderId()+","+order.getId());
                groupMember.setMemberId(groupMember.getMemberId()+","+order.getMemberId());
                groupMemberMapper.updateById(groupMember);
            }

        } else {
            return new CommonResult().failed("活动已经结束");
        }
        for (OmsOrderItem orderItem : orderItemList) {
            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getOrderSn());
        }
        orderItemService.saveBatch(orderItemList);
        //如使用优惠券更新优惠券使用状态
        if (orderParam.getCouponId() != null) {
            updateCouponStatus(orderParam.getCouponId(), currentMember.getId(), 1);
        }
        //如使用积分需要扣除积分
        if (orderParam.getUseIntegration() != null) {
            order.setUseIntegration(orderParam.getUseIntegration());
            memberService.updateIntegration(currentMember.getId(), currentMember.getIntegration() - orderParam.getUseIntegration());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItemList", orderItemList);

        String platform = orderParam.getPlatform();
        if ("1".equals(platform)) {
            push(currentMember, order, orderParam.getPage(), orderParam.getFormId(), name);
        }
        return new CommonResult().success("下单成功", result);
    }

    private void createCartObj(OrderParam orderParam, List<OmsCartItem> list, OmsCartItem cartItem, PmsProduct pmsProduct) {
        if (ValidatorUtils.notEmpty(orderParam.getSkuId())) {
            PmsSkuStock pmsSkuStock = skuStockMapper.selectById(orderParam.getSkuId());
            checkGoods(pmsProduct, false, 1);
            checkSkuGoods(pmsSkuStock, 1);
            cartItem.setProductId(pmsSkuStock.getProductId());
            cartItem.setMemberId(orderParam.getMemberId());
            cartItem.setProductSkuId(pmsSkuStock.getId());
            cartItem.setChecked(1);
            cartItem.setPrice(pmsSkuStock.getPrice());
            cartItem.setProductSkuCode(pmsSkuStock.getSkuCode());
            cartItem.setQuantity(orderParam.getTotal());
            cartItem.setProductAttr(pmsSkuStock.getMeno());
            cartItem.setProductPic(pmsSkuStock.getPic());
            cartItem.setSp1(pmsSkuStock.getSp1());
            cartItem.setSp2(pmsSkuStock.getSp2());
            cartItem.setSp3(pmsSkuStock.getSp3());
            cartItem.setProductName(pmsSkuStock.getProductName());
            cartItem.setProductCategoryId(pmsProduct.getProductCategoryId());
            cartItem.setProductBrand(pmsProduct.getBrandName());
            cartItem.setCreateDate(new Date());

        } else {
            checkGoods(pmsProduct, true, orderParam.getTotal());
            cartItem.setProductId(orderParam.getGoodsId());
            cartItem.setMemberId(orderParam.getMemberId());
            cartItem.setChecked(1);
            cartItem.setPrice(pmsProduct.getPrice());
            cartItem.setProductName(pmsProduct.getName());
            cartItem.setQuantity(orderParam.getTotal());
            cartItem.setProductPic(pmsProduct.getPic());
            cartItem.setCreateDate(new Date());
            cartItem.setMemberId(orderParam.getMemberId());
            cartItem.setProductCategoryId(pmsProduct.getProductCategoryId());
            cartItem.setProductBrand(pmsProduct.getBrandName());

        }
        list.add(cartItem);
    }

    private OmsOrderItem createOrderItem(OmsCartItem cartPromotionItem) {
        OmsOrderItem orderItem = new OmsOrderItem();
        orderItem.setProductAttr(cartPromotionItem.getProductAttr());
        orderItem.setProductId(cartPromotionItem.getProductId());
        orderItem.setProductName(cartPromotionItem.getProductName());
        orderItem.setProductPic(cartPromotionItem.getProductPic());
        orderItem.setProductAttr(cartPromotionItem.getProductAttr());
        orderItem.setProductBrand(cartPromotionItem.getProductBrand());
        orderItem.setProductSn(cartPromotionItem.getProductSn());
        orderItem.setProductPrice(cartPromotionItem.getPrice());
        orderItem.setProductQuantity(cartPromotionItem.getQuantity());
        orderItem.setProductSkuId(cartPromotionItem.getProductSkuId());
        orderItem.setProductSkuCode(cartPromotionItem.getProductSkuCode());
        orderItem.setProductCategoryId(cartPromotionItem.getProductCategoryId());
           /* orderItem.setPromotionAmount(cartPromotionItem.getReduceAmount());
            orderItem.setPromotionName(cartPromotionItem.getPromotionMessage());
            orderItem.setGiftIntegration(cartPromotionItem.getIntegration());
            orderItem.setGiftGrowth(cartPromotionItem.getGrowth());*/
        return orderItem;
    }

    private OmsOrder createOrderObj(OrderParam orderParam, UmsMember currentMember, List<OmsOrderItem> orderItemList, UmsMemberReceiveAddress address) {
        OmsOrder order = new OmsOrder();
        order.setDiscountAmount(new BigDecimal(0));
        order.setTotalAmount(calcTotalAmount(orderItemList));
        order.setFreightAmount(new BigDecimal(0));
        order.setPromotionAmount(calcPromotionAmount(orderItemList));
        order.setPromotionInfo(getOrderPromotionInfo(orderItemList));
        if (ValidatorUtils.notEmpty(orderParam.getGroupId())) {
            order.setGroupId(orderParam.getGroupId());
        }
        if (orderParam.getCouponId() == null) {
            order.setCouponAmount(new BigDecimal(0));
        } else {
            order.setCouponId(orderParam.getCouponId());
            order.setCouponAmount(calcCouponAmount(orderItemList));
        }
        if (orderParam.getUseIntegration() == null) {
            order.setIntegration(0);
            order.setIntegrationAmount(new BigDecimal(0));
        } else {
            order.setIntegration(orderParam.getUseIntegration());
            order.setIntegrationAmount(calcIntegrationAmount(orderItemList));
        }
        order.setPayAmount(calcPayAmount(order));
        //转化为订单信息并插入数据库
        order.setCreateTime(new Date());
        order.setMemberUsername(currentMember.getUsername());
        //支付方式：0->未支付；1->支付宝；2->微信
        order.setPayType(orderParam.getPayType());
        //订单来源：0->PC订单；1->app订单
        order.setSourceType(1);
        //订单状态：订单状态：1->待付款；2->待发货；3->已发货；4->已完成；5->售后订单 6->已关闭；
        order.setStatus(1);
        //订单类型：0->正常订单；1->秒杀订单
        order.setOrderType(0);
        //收货人信息：姓名、电话、邮编、地址
        if (address!=null){
            order.setReceiverId(address.getId());
            order.setReceiverName(address.getName());
            order.setReceiverPhone(address.getPhoneNumber());
            order.setReceiverPostCode(address.getPostCode());
            order.setReceiverProvince(address.getProvince());
            order.setReceiverCity(address.getCity());
            order.setReceiverRegion(address.getRegion());
            order.setReceiverDetailAddress(address.getDetailAddress());
        }

        //0->未确认；1->已确认
        order.setConfirmStatus(0);
        order.setDeleteStatus(0);
        order.setMemberId(orderParam.getMemberId());
        //生成订单号
        order.setOrderSn(generateOrderSn(order));
        return order;
    }
    @Override
    @Transactional
    public boolean closeOrder(OmsOrder order){
        releaseStock(order);
        order.setStatus(OrderStatus.CLOSED.getValue());
        return orderMapper.updateById(order) > 0;
    }

    @Override
    public void releaseStock(OmsOrder order){
        List<OmsOrderItem> itemList = orderItemService.list(new QueryWrapper<OmsOrderItem>().eq("order_id", order.getId()));
        if (itemList != null && itemList.size() > 0) {
            for (OmsOrderItem item : itemList) {
                PmsProduct goods = productService.getById(item.getProductId());
                if (goods != null && goods.getId() != null ) {
                    redisService.remove(String.format(Rediskey.GOODSDETAIL, goods.getId() + ""));
                    goods.setStock(goods.getStock()+1);
                    goods.setSale(goods.getSale()-1);
                    productService.updateById(goods);
                    if (!ValidatorUtils.empty(item.getProductSkuId()) && item.getProductSkuId() > 0) {
                        PmsSkuStock skuStock = new PmsSkuStock();
                        skuStock.setId(item.getProductSkuId());
                        skuStock.setStock(skuStock.getStock()+1);
                        skuStock.setSale(skuStock.getSale()-1);
                        skuStockMapper.updateById(skuStock);
                     }
                }

            }
        }
    }
    @Override
    @Transactional
    public  Object jifenPay(OrderParam orderParam){
        UmsMember member= memberService.getById(orderParam.getMemberId());
        PmsGifts gifts = giftsService.getById(orderParam.getGoodsId());
        if(gifts.getPrice().intValue()>member.getIntegration()){
            return new CommonResult().failed("积分不足！");
        }else {
           // UmsMemberReceiveAddress address = addressService.getById(orderParam.getAddressId());

            OmsOrderItem orderItem = new OmsOrderItem();
            orderItem.setProductId(orderParam.getGoodsId());
            orderItem.setProductName(gifts.getTitle());
            orderItem.setProductPic(gifts.getIcon());
            orderItem.setProductPrice(gifts.getPrice());
            orderItem.setProductQuantity(1);
            orderItem.setProductCategoryId(gifts.getCategoryId());
            List<OmsOrderItem> omsOrderItemList = new ArrayList<>();
            omsOrderItemList.add(orderItem);
            OmsOrder order = createOrderObj(orderParam, member, omsOrderItemList, null);
            order.setOrderType(2);
            order.setStatus(OrderStatus.TO_DELIVER.getValue());
            order.setPayType(3);
            orderService.save(order);
            orderItem.setOrderId(order.getId());
            orderItemService.save(orderItem);
            member.setIntegration(member.getIntegration()-gifts.getPrice().intValue());
            memberService.updateById(member);

        }
        return new CommonResult().success("兑换成功");
    }
    @Override
    public CommonResult paySuccess(Long orderId) {
        //修改订单支付状态
        OmsOrder order = new OmsOrder();
        order.setId(orderId);
        order.setStatus(2);
        order.setPaymentTime(new Date());
        orderService.updateById(order);
        //恢复所有下单商品的锁定库存，扣减真实库存
        OmsOrderItem queryO = new OmsOrderItem();
        queryO.setOrderId(orderId);
        List<OmsOrderItem> list = orderItemService.list(new QueryWrapper<>(queryO));
        int count = orderMapper.updateSkuStock(list);
        return new CommonResult().success("支付成功", count);
    }

    /**
     * 推送消息
     */
    public void push(UmsMember umsMember, OmsOrder order, String page, String formId, String name) {
        log.info("发送模版消息：userId=" + umsMember.getId() + ",orderId=" + order.getId() + ",formId=" + formId);
        if (StringUtils.isEmpty(formId)) {
            log.error("发送模版消息：userId=" + umsMember.getId() + ",orderId=" + order.getId() + ",formId=" + formId);
        }
        String accessToken = null;
        try {
            accessToken = wechatApiService.getAccessToken();

            String templateId = wxAppletProperties.getTemplateId();
            Map<String, TemplateData> param = new HashMap<String, TemplateData>();
            param.put("keyword1", new TemplateData(DateUtils.format(order.getCreateTime(), "yyyy-MM-dd"), "#EE0000"));

            param.put("keyword2", new TemplateData(name, "#EE0000"));
            param.put("keyword3", new TemplateData(order.getOrderSn(), "#EE0000"));
            param.put("keyword3", new TemplateData(order.getPayAmount() + "", "#EE0000"));

            JSONObject jsonObject = JSONObject.fromObject(param);
            //调用发送微信消息给用户的接口    ********这里写自己在微信公众平台拿到的模板ID
            WX_TemplateMsgUtil.sendWechatMsgToUser(umsMember.getWeixinOpenid(), templateId, page + "?id=" + order.getId(),
                    formId, jsonObject, accessToken);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public CommonResult cancelTimeOutOrder() {
        OmsOrderSetting orderSetting = orderSettingMapper.selectOne(new QueryWrapper<>());
        if (orderSetting!=null){
            //查询超时、未支付的订单及订单详情
            List<OmsOrderDetail> timeOutOrders = null;
                    //orderMapper.getTimeOutOrders(orderSetting.getNormalOrderOvertime());
            if (CollectionUtils.isEmpty(timeOutOrders)) {
                return new CommonResult().failed("暂无超时订单");
            }
            //修改订单状态为交易取消
            List<Long> ids = new ArrayList<>();
            for (OmsOrderDetail timeOutOrder : timeOutOrders) {
                ids.add(timeOutOrder.getId());
            }
            orderMapper.updateOrderStatus(ids, 4);
            for (OmsOrderDetail timeOutOrder : timeOutOrders) {
                //解除订单商品库存锁定
                orderMapper.releaseSkuStockLock(timeOutOrder.getOrderItemList());
                //修改优惠券使用状态
                updateCouponStatus(timeOutOrder.getCouponId(), timeOutOrder.getMemberId(), 0);
                //返还使用积分
                if (timeOutOrder.getUseIntegration() != null) {
                    UmsMember member = memberService.getById(timeOutOrder.getMemberId());
                    memberService.updateIntegration(timeOutOrder.getMemberId(), member.getIntegration() + timeOutOrder.getUseIntegration());
                }
            }
        }
        return new CommonResult().success(null);
    }

    @Override
    public void cancelOrder(Long orderId) {
        //查询为付款的取消订单

        OmsOrder cancelOrder = orderMapper.selectById(orderId);
        if (cancelOrder != null) {
            //修改订单状态为取消
            cancelOrder.setStatus(4);
            orderMapper.updateById(cancelOrder);
            OmsOrderItem queryO = new OmsOrderItem();
            queryO.setOrderId(orderId);
            List<OmsOrderItem> list = orderItemService.list(new QueryWrapper<>(queryO));
            //解除订单商品库存锁定
            orderMapper.releaseSkuStockLock(list);
            //修改优惠券使用状态
            updateCouponStatus(cancelOrder.getCouponId(), cancelOrder.getMemberId(), 0);
            //返还使用积分
            if (cancelOrder.getUseIntegration() != null) {
                UmsMember member = memberService.getById(cancelOrder.getMemberId());
                memberService.updateIntegration(cancelOrder.getMemberId(), member.getIntegration() + cancelOrder.getUseIntegration());
            }
        }
    }

    @Override
    public Object preSingelOrder(GroupAndOrderVo orderParam) {
        ConfirmOrderResult result = new ConfirmOrderResult();
        result.setGroupAndOrderVo(orderParam);
        PmsProduct goods = productService.getById(orderParam.getGoodsId());
        result.setGoods(goods);
        //获取用户收货地址列表
        List<UmsMemberReceiveAddress> memberReceiveAddressList = addressService.list(new QueryWrapper<>());
        result.setMemberReceiveAddressList(memberReceiveAddressList);
        UmsMemberReceiveAddress address = addressService.getDefaultItem();

        result.setAddress(address);
        return result;
    }

    /**
     * 推送消息
     */
    public void push(GroupAndOrderVo umsMember, OmsOrder order, String page, String formId) {
        log.info("发送模版消息：userId=" + umsMember.getMemberId() + ",orderId=" + order.getId() + ",formId=" + formId);
        if (StringUtils.isEmpty(formId)) {
            log.error("发送模版消息：userId=" + umsMember.getMemberId() + ",orderId=" + order.getId() + ",formId=" + formId);
        }
        String accessToken = null;
        try {
            accessToken = wechatApiService.getAccessToken();

            String templateId = wxAppletProperties.getTemplateId();
            Map<String, TemplateData> param = new HashMap<String, TemplateData>();
            param.put("keyword1", new TemplateData(DateUtils.format(order.getCreateTime(), "yyyy-MM-dd"), "#EE0000"));

            param.put("keyword2", new TemplateData(order.getGoodsName(), "#EE0000"));
            param.put("keyword3", new TemplateData(order.getOrderSn(), "#EE0000"));
            param.put("keyword3", new TemplateData(order.getPayAmount() + "", "#EE0000"));

            JSONObject jsonObject = JSONObject.fromObject(param);
            //调用发送微信消息给用户的接口    ********这里写自己在微信公众平台拿到的模板ID
            WX_TemplateMsgUtil.sendWechatMsgToUser(umsMember.getWxid(), templateId, page + "?id=" + order.getId(),
                    formId, jsonObject, accessToken);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Transactional
    @Override
    public Object generateSingleOrder(GroupAndOrderVo orderParam, UmsMember member) {
        String type = orderParam.getType();
        orderParam.setMemberId(member.getId()+"");
        orderParam.setName(member.getIcon());
        PmsProduct goods = productService.getById(orderParam.getGoodsId());

        if (goods.getStock() < 0) {
            return new CommonResult().failed("库存不足，无法下单");
        }


        //根据商品合计、运费、活动优惠、优惠券、积分计算应付金额
        OmsOrder order = new OmsOrder();
        order.setDiscountAmount(new BigDecimal(0));
        order.setTotalAmount(goods.getPrice());
        order.setPayAmount(goods.getPrice());
        order.setFreightAmount(new BigDecimal(0));
        order.setPromotionAmount(new BigDecimal(0));

        order.setSupplyId(goods.getSupplyId());
        order.setCouponAmount(new BigDecimal(0));

        order.setIntegration(0);
        order.setIntegrationAmount(new BigDecimal(0));


        order.setGoodsId(goods.getId());
        order.setGoodsName(order.getGoodsName());
        //转化为订单信息并插入数据库
        order.setMemberId(Long.parseLong(orderParam.getMemberId()));
        order.setCreateTime(new Date());
        order.setMemberUsername(member.getUsername());
        //支付方式：0->未支付；1->支付宝；2->微信
        order.setPayType(orderParam.getPayType());
        //订单来源：0->PC订单；1->app订单
        order.setSourceType(orderParam.getSourceType());
        //订单状态：1->待付款；2->待发货；3->已发货；4->已完成；5->售后订单 6->已关闭；
        order.setStatus(1);
        //订单类型：0->正常订单；1->秒杀订单
        order.setOrderType(orderParam.getOrderType());
        //收货人信息：姓名、电话、邮编、地址
        UmsMemberReceiveAddress address = addressService.getById(orderParam.getAddressId());
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverDetailAddress(address.getDetailAddress());
        //0->未确认；1->已确认
        order.setConfirmStatus(0);
        order.setDeleteStatus(0);
        //计算赠送积分
        order.setIntegration(0);
        //计算赠送成长值
        order.setGrowth(0);
        //生成订单号
        order.setOrderSn(generateOrderSn(order));
        SmsGroup group = groupService.getById(orderParam.getGroupId());
        if (group != null) {
            order.setPayAmount(group.getGroupPrice());
        }
        // TODO: 2018/9/3 bill_*,delivery_*
        //插入order表和order_item表
        this.save(order);


        // 0 下单 1 拼团 2 发起拼团

        if ("1".equals(type)) {
            SmsGroupMember sm = new SmsGroupMember();
            sm.setGroupId(orderParam.getGroupId());
            sm.setMemberId(orderParam.getMemberId());
            List<SmsGroupMember> smsGroupMemberList = groupMemberService.list(new QueryWrapper<>(sm));
            if (smsGroupMemberList != null && smsGroupMemberList.size() > 0) {
                return new CommonResult().failed("你已经参加此拼团");
            }
            Date endTime = DateUtils.convertStringToDate(DateUtils.addHours(group.getEndTime(), group.getHours()), "yyyy-MM-dd HH:mm:ss");
            Long nowT = System.currentTimeMillis();
            if (nowT > group.getStartTime().getTime() && nowT < endTime.getTime()) {

                orderParam.setStatus(2);
                orderParam.setCreateTime(new Date());
                orderParam.setOrderId(order.getId()+"");
                groupMemberService.save(orderParam);
            } else {
                return new CommonResult().failed("活动已经结束");
            }
        } else if ("2".equals(type)) {
            group = groupService.getById(orderParam.getGroupId());
            Date endTime = DateUtils.convertStringToDate(DateUtils.addHours(group.getEndTime(), group.getHours()), "yyyy-MM-dd HH:mm:ss");
            Long nowT = System.currentTimeMillis();
            if (nowT > group.getStartTime().getTime() && nowT < endTime.getTime()) {

                orderParam.setStatus(2);
                orderParam.setCreateTime(new Date());
                orderParam.setOrderId(order.getId()+"");

                groupMemberService.save(orderParam);
            } else {
                return new CommonResult().failed("活动已经结束");
            }

        }
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);

        if (orderParam.getSourceType() == 1) {
            push(orderParam, order, orderParam.getPage(), orderParam.getFormId());
        }
        return new CommonResult().success("下单成功", result);
    }


    /**
     * 生成18位订单编号:8位日期+2位平台号码+2位支付方式+6位以上自增id
     */
    private String generateOrderSn(OmsOrder order) {

        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis());
        sb.append(String.format("%02d", order.getSourceType()));
        sb.append(String.format("%02d", order.getPayType()));
        sb.append(order.getMemberId());
        return sb.toString();
    }

    /**
     * 计算总金额
     */
    private BigDecimal calcTotalAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsOrderItem item : orderItemList) {
            totalAmount = totalAmount.add(item.getProductPrice().multiply(new BigDecimal(item.getProductQuantity())));
        }
        return totalAmount;
    }

    /**
     * 锁定下单商品的所有库存
     */
    private void lockStock(List<OmsCartItem> cartPromotionItemList) {
        for (OmsCartItem cartPromotionItem : cartPromotionItemList) {
            if (ValidatorUtils.notEmpty(cartPromotionItem.getProductSkuId())) {
                PmsSkuStock skuStock = skuStockMapper.selectById(cartPromotionItem.getProductSkuId());
                skuStock.setLockStock(skuStock.getLockStock() + cartPromotionItem.getQuantity());
                skuStockMapper.updateById(skuStock);
            } else {
               /* PmsProduct skuStock = productService.getById(cartPromotionItem.getProductId());
                skuStock.setLockStock(skuStock.getLockStock() + cartPromotionItem.getQuantity());
                skuStockMapper.updateById(skuStock);*/
            }

        }
    }

    /**
     * 判断下单商品是否都有库存
     */
    private boolean hasStock(List<OmsCartItem> cartPromotionItemList) {

        return true;
    }

    /**
     * 计算购物车中商品的价格
     */
    private ConfirmOrderResult.CalcAmount calcCartAmount(List<OmsCartItem> cartPromotionItemList) {
        ConfirmOrderResult.CalcAmount calcAmount = new ConfirmOrderResult.CalcAmount();
        calcAmount.setFreightAmount(new BigDecimal(0));
        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal promotionAmount = new BigDecimal("0");
        for (OmsCartItem cartPromotionItem : cartPromotionItemList) {
            totalAmount = totalAmount.add(cartPromotionItem.getPrice().multiply(new BigDecimal(cartPromotionItem.getQuantity())));
          //  promotionAmount = promotionAmount.add(cartPromotionItem.getReduceAmount().multiply(new BigDecimal(cartPromotionItem.getQuantity())));
        }
        calcAmount.setTotalAmount(totalAmount);
        calcAmount.setPromotionAmount(promotionAmount);
        calcAmount.setPayAmount(totalAmount.subtract(promotionAmount));
        return calcAmount;
    }


    /**
     * 删除下单商品的购物车信息
     */
    private void deleteCartItemList(List<OmsCartItem> cartPromotionItemList, UmsMember currentMember) {
        List<Long> ids = new ArrayList<>();
        for (OmsCartItem cartPromotionItem : cartPromotionItemList) {
            ids.add(cartPromotionItem.getId());
        }
        cartItemService.delete(currentMember.getId(), ids);
    }





    /**
     * 将优惠券信息更改为指定状态
     *
     * @param couponId  优惠券id
     * @param memberId  会员id
     * @param useStatus 0->未使用；1->已使用
     */
    private void updateCouponStatus(Long couponId, Long memberId, Integer useStatus) {
        if (couponId == null) {
            return;
        }
        //查询第一张优惠券
        SmsCouponHistory queryC = new SmsCouponHistory();
        queryC.setCouponId(couponId);
        if (useStatus == 0) {
            queryC.setUseStatus(1);
        } else {
            queryC.setUseStatus(0);
        }
        List<SmsCouponHistory> couponHistoryList = couponHistoryService.list(new QueryWrapper<>(queryC));
        if (!CollectionUtils.isEmpty(couponHistoryList)) {
            SmsCouponHistory couponHistory = couponHistoryList.get(0);
            couponHistory.setUseTime(new Date());
            couponHistory.setUseStatus(useStatus);
            couponHistoryService.updateById(couponHistory);
        }
    }

    private void handleRealAmount(List<OmsOrderItem> orderItemList) {
        for (OmsOrderItem orderItem : orderItemList) {
            //原价-促销价格-优惠券抵扣-积分抵扣
            BigDecimal realAmount = orderItem.getProductPrice()
                    .subtract(orderItem.getPromotionAmount())
                    .subtract(orderItem.getCouponAmount())
                    .subtract(orderItem.getIntegrationAmount());
            orderItem.setRealAmount(realAmount);
        }
    }

    /**
     * 获取订单促销信息
     */
    private String getOrderPromotionInfo(List<OmsOrderItem> orderItemList) {
        StringBuilder sb = new StringBuilder();
        for (OmsOrderItem orderItem : orderItemList) {
            sb.append(orderItem.getPromotionName());
            sb.append(",");
        }
        String result = sb.toString();
        if (result.endsWith(",")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * 计算订单应付金额
     */
    private BigDecimal calcPayAmount(OmsOrder order) {
        //总金额+运费-促销优惠-优惠券优惠-积分抵扣
        BigDecimal payAmount = order.getTotalAmount()
                .add(order.getFreightAmount())
                .subtract(order.getPromotionAmount())
                .subtract(order.getCouponAmount())
                .subtract(order.getIntegrationAmount());
        return payAmount;
    }

    /**
     * 计算订单优惠券金额
     */
    private BigDecimal calcIntegrationAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal integrationAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getIntegrationAmount() != null) {
                integrationAmount = integrationAmount.add(orderItem.getIntegrationAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return integrationAmount;
    }

    /**
     * 计算订单优惠券金额
     */
    private BigDecimal calcCouponAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal couponAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getCouponAmount() != null) {
                couponAmount = couponAmount.add(orderItem.getCouponAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return couponAmount;
    }

    /**
     * 计算订单活动优惠
     */
    private BigDecimal calcPromotionAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal promotionAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getPromotionAmount() != null) {
                promotionAmount = promotionAmount.add(orderItem.getPromotionAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return promotionAmount;
    }

    /**
     * 获取可用积分抵扣金额
     *
     * @param useIntegration 使用的积分数量
     * @param totalAmount    订单总金额
     * @param currentMember  使用的用户
     * @param hasCoupon      是否已经使用优惠券
     */
    private BigDecimal getUseIntegrationAmount(Integer useIntegration, BigDecimal totalAmount, UmsMember currentMember, boolean hasCoupon) {
        BigDecimal zeroAmount = new BigDecimal(0);
        //判断用户是否有这么多积分
        if (useIntegration.compareTo(currentMember.getIntegration()) > 0) {
            return zeroAmount;
        }
        //根据积分使用规则判断使用可用
        //是否可用于优惠券共用
        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectById(1L);
        if (hasCoupon && integrationConsumeSetting.getCouponStatus().equals(0)) {
            //不可与优惠券共用
            return zeroAmount;
        }
        //是否达到最低使用积分门槛
        if (useIntegration.compareTo(integrationConsumeSetting.getUseUnit()) < 0) {
            return zeroAmount;
        }
        //是否超过订单抵用最高百分比
        BigDecimal integrationAmount = new BigDecimal(useIntegration).divide(new BigDecimal(integrationConsumeSetting.getUseUnit()), 2, RoundingMode.HALF_EVEN);
        BigDecimal maxPercent = new BigDecimal(integrationConsumeSetting.getMaxPercentPerOrder()).divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN);
        if (integrationAmount.compareTo(totalAmount.multiply(maxPercent)) > 0) {
            return zeroAmount;
        }
        return integrationAmount;
    }

    /**
     * 对优惠券优惠进行处理
     *
     * @param orderItemList       order_item列表
     * @param couponHistoryDetail 可用优惠券详情
     */
    private void handleCouponAmount(List<OmsOrderItem> orderItemList, SmsCouponHistoryDetail couponHistoryDetail) {
        SmsCoupon coupon = couponHistoryDetail.getCoupon();
        if (coupon.getUseType().equals(0)) {
            //全场通用
            calcPerCouponAmount(orderItemList, coupon);
        } else if (coupon.getUseType().equals(1)) {
            //指定分类
            List<OmsOrderItem> couponOrderItemList = getCouponOrderItemByRelation(couponHistoryDetail, orderItemList, 0);
            calcPerCouponAmount(couponOrderItemList, coupon);
        } else if (coupon.getUseType().equals(2)) {
            //指定商品
            List<OmsOrderItem> couponOrderItemList = getCouponOrderItemByRelation(couponHistoryDetail, orderItemList, 1);
            calcPerCouponAmount(couponOrderItemList, coupon);
        }
    }

    /**
     * 对每个下单商品进行优惠券金额分摊的计算
     *
     * @param orderItemList 可用优惠券的下单商品商品
     */
    private void calcPerCouponAmount(List<OmsOrderItem> orderItemList, SmsCoupon coupon) {
        BigDecimal totalAmount = calcTotalAmount(orderItemList);
        for (OmsOrderItem orderItem : orderItemList) {
            //(商品价格/可用商品总价)*优惠券面额
            BigDecimal couponAmount = orderItem.getProductPrice().divide(totalAmount, 3, RoundingMode.HALF_EVEN).multiply(coupon.getAmount());
            orderItem.setCouponAmount(couponAmount);
        }
    }

    /**
     * 获取与优惠券有关系的下单商品
     *
     * @param couponHistoryDetail 优惠券详情
     * @param orderItemList       下单商品
     * @param type                使用关系类型：0->相关分类；1->指定商品
     */
    private List<OmsOrderItem> getCouponOrderItemByRelation(SmsCouponHistoryDetail couponHistoryDetail, List<OmsOrderItem> orderItemList, int type) {
        List<OmsOrderItem> result = new ArrayList<>();
        if (type == 0) {
            List<Long> categoryIdList = new ArrayList<>();
            for (SmsCouponProductCategoryRelation productCategoryRelation : couponHistoryDetail.getCategoryRelationList()) {
                categoryIdList.add(productCategoryRelation.getProductCategoryId());
            }
            for (OmsOrderItem orderItem : orderItemList) {
                if (categoryIdList.contains(orderItem.getProductCategoryId())) {
                    result.add(orderItem);
                } else {
                    orderItem.setCouponAmount(new BigDecimal(0));
                }
            }
        } else if (type == 1) {
            List<Long> productIdList = new ArrayList<>();
            for (SmsCouponProductRelation productRelation : couponHistoryDetail.getProductRelationList()) {
                productIdList.add(productRelation.getProductId());
            }
            for (OmsOrderItem orderItem : orderItemList) {
                if (productIdList.contains(orderItem.getProductId())) {
                    result.add(orderItem);
                } else {
                    orderItem.setCouponAmount(new BigDecimal(0));
                }
            }
        }
        return result;
    }

    /**
     * 获取该用户可以使用的优惠券
     *
     * @param cartPromotionItemList 购物车优惠列表
     * @param couponId              使用优惠券id
     */
    private SmsCouponHistoryDetail getUseCoupon(List<OmsCartItem> cartPromotionItemList, Long couponId) {
        List<SmsCouponHistoryDetail> couponHistoryDetailList = couponService.listCart(cartPromotionItemList, 1);
        for (SmsCouponHistoryDetail couponHistoryDetail : couponHistoryDetailList) {
            if (couponHistoryDetail.getCoupon().getId().equals(couponId)) {
                return couponHistoryDetail;
            }
        }
        return null;
    }

    @Override
    public ConfirmOrderResult addGroup(OrderParam orderParam) {
        List<OmsCartItem> list = new ArrayList<>();
        if (ValidatorUtils.empty(orderParam.getTotal())) {
            orderParam.setTotal(1);
        }
        OmsCartItem cartItem = new OmsCartItem();
        PmsProduct pmsProduct = productService.getById(orderParam.getGoodsId());
        createCartObj(orderParam, list, cartItem, pmsProduct);
        ConfirmOrderResult result = new ConfirmOrderResult();
        //获取购物车信息

        result.setCartPromotionItemList(list);
        //获取用户收货地址列表
        UmsMemberReceiveAddress queryU = new UmsMemberReceiveAddress();
        queryU.setMemberId(orderParam.getMemberId());
        List<UmsMemberReceiveAddress> memberReceiveAddressList = addressService.list(new QueryWrapper<>(queryU));
        result.setMemberReceiveAddressList(memberReceiveAddressList);
        UmsMemberReceiveAddress address = addressService.getDefaultItem();
        //获取用户可用优惠券列表
        List<SmsCouponHistoryDetail> couponHistoryDetailList = couponService.listCart(list, 1);
        result.setCouponHistoryDetailList(couponHistoryDetailList);
        UmsMember member = memberService.getById(orderParam.getMemberId());
        //获取用户积分
        result.setMemberIntegration(member.getIntegration());
        //获取积分使用规则
        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectById(1L);
        result.setIntegrationConsumeSetting(integrationConsumeSetting);
        //计算总金额、活动优惠、应付金额
        ConfirmOrderResult.CalcAmount calcAmount = calcCartAmount(list);
        result.setCalcAmount(calcAmount);
        result.setAddress(address);
        return result;
    }

    @Override
    public Object addCart(CartParam cartParam) {
        if (ValidatorUtils.empty(cartParam.getTotal())) {
            cartParam.setTotal(1);
        }
        OmsCartItem cartItem = new OmsCartItem();
        PmsProduct pmsProduct = productService.getById(cartParam.getGoodsId());
        if (ValidatorUtils.notEmpty(cartParam.getSkuId())) {
            PmsSkuStock pmsSkuStock = skuStockMapper.selectById(cartParam.getSkuId());
            checkGoods(pmsProduct, false, cartParam.getTotal());
            checkSkuGoods(pmsSkuStock, cartParam.getTotal());
            cartItem.setProductId(pmsSkuStock.getProductId());
            cartItem.setMemberId(cartParam.getMemberId());
            cartItem.setProductSkuId(pmsSkuStock.getId());
            OmsCartItem existCartItem = cartItemMapper.selectOne(new QueryWrapper<>(cartItem));
            if (existCartItem == null) {
                cartItem.setChecked(1);
                cartItem.setMemberId(cartParam.getMemberId());
                cartItem.setPrice(pmsSkuStock.getPrice());
                cartItem.setProductSkuCode(pmsSkuStock.getSkuCode());
                cartItem.setQuantity(cartParam.getTotal());
                cartItem.setProductAttr(pmsSkuStock.getMeno());
                cartItem.setProductPic(pmsSkuStock.getPic());
                cartItem.setSp1(pmsSkuStock.getSp1());
                cartItem.setSp2(pmsSkuStock.getSp2());
                cartItem.setSp3(pmsSkuStock.getSp3());
                cartItem.setProductName(pmsSkuStock.getProductName());
                cartItem.setProductCategoryId(pmsProduct.getProductCategoryId());
                cartItem.setProductBrand(pmsProduct.getBrandName());
                cartItem.setCreateDate(new Date());
                cartItemMapper.insert(cartItem);
            } else {
                existCartItem.setPrice(pmsSkuStock.getPrice());
                existCartItem.setModifyDate(new Date());
                existCartItem.setQuantity(existCartItem.getQuantity() + cartParam.getTotal());
                cartItemMapper.updateById(existCartItem);
                return new CommonResult().success(existCartItem);
            }
        } else {
            checkGoods(pmsProduct, true, cartParam.getTotal());
            cartItem.setProductId(cartParam.getGoodsId());
            cartItem.setMemberId(cartParam.getMemberId());
            OmsCartItem existCartItem = cartItemMapper.selectOne(new QueryWrapper<>(cartItem));
            if (existCartItem == null) {
                cartItem.setChecked(1);
                cartItem.setPrice(pmsProduct.getPrice());
                cartItem.setProductName(pmsProduct.getName());
                cartItem.setQuantity(cartParam.getTotal());
                cartItem.setProductPic(pmsProduct.getPic());
                cartItem.setCreateDate(new Date());
                cartItem.setMemberId(cartParam.getMemberId());
                cartItem.setProductCategoryId(pmsProduct.getProductCategoryId());
                cartItem.setProductBrand(pmsProduct.getBrandName());
                cartItemMapper.insert(cartItem);
            } else {
                existCartItem.setPrice(pmsProduct.getPrice());
                existCartItem.setModifyDate(new Date());
                existCartItem.setQuantity(existCartItem.getQuantity() + cartParam.getTotal());
                cartItemMapper.updateById(existCartItem);
                return new CommonResult().success(existCartItem);
            }
        }
        return new CommonResult().success(cartItem);
    }

    private void checkGoods(PmsProduct goods, boolean falg, int count) {
        if (goods == null || goods.getId() == null) {
            throw new ApiMallPlusException("商品已删除");
        }
        if (falg && (goods.getStock() <= 0 || goods.getStock() < count)) {
            throw new ApiMallPlusException("库存不足!");
        }
    }

    private void checkSkuGoods(PmsSkuStock goods, int count) {
        if (goods == null || goods.getId() == null) {
            throw new ApiMallPlusException("商品已删除");
        }
        if (goods.getStock() <= 0 || goods.getStock() < count) {
            throw new ApiMallPlusException("库存不足!");
        }
    }
}
