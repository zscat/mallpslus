package com.zscat.mallplus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zscat.mallplus.annotation.IgnoreAuth;
import com.zscat.mallplus.annotation.SysLog;
import com.zscat.mallplus.cms.entity.CmsSubject;
import com.zscat.mallplus.cms.service.ICmsSubjectService;
import com.zscat.mallplus.constant.RedisKey;
import com.zscat.mallplus.pms.entity.PmsProductCategory;
import com.zscat.mallplus.pms.entity.PmsSmallNaviconCategory;
import com.zscat.mallplus.pms.service.IPmsProductCategoryService;
import com.zscat.mallplus.pms.service.IPmsSmallNaviconCategoryService;
import com.zscat.mallplus.sms.entity.*;
import com.zscat.mallplus.sms.mapper.SmsFlashPromotionSessionMapper;
import com.zscat.mallplus.sms.mapper.SmsHomeNewProductMapper;
import com.zscat.mallplus.sms.mapper.SmsHomeRecommendProductMapper;
import com.zscat.mallplus.sms.service.*;
import com.zscat.mallplus.oms.entity.OmsOrder;
import com.zscat.mallplus.oms.service.IOmsOrderService;
import com.zscat.mallplus.pms.entity.PmsProduct;
import com.zscat.mallplus.pms.entity.PmsProductAttributeCategory;
import com.zscat.mallplus.pms.service.IPmsProductAttributeCategoryService;
import com.zscat.mallplus.pms.service.IPmsProductService;
import com.zscat.mallplus.single.ApiBaseAction;
import com.zscat.mallplus.sms.vo.HomeProductAttr;
import com.zscat.mallplus.sms.vo.SmsFlashSessionInfo;
import com.zscat.mallplus.ums.entity.UmsMember;
import com.zscat.mallplus.ums.service.IUmsMemberService;
import com.zscat.mallplus.ums.service.RedisService;
import com.zscat.mallplus.util.JsonUtil;
import com.zscat.mallplus.util.UserUtils;
import com.zscat.mallplus.utils.CommonResult;
import com.zscat.mallplus.vo.*;
import com.zscat.mallplus.vo.pms.CateProduct;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 会员登录注册管理Controller
 * https://github.com/shenzhuan/mallplus on 2018/8/3.
 */
@RestController
@Api(tags = "AppletMemberController", description = "小程序登录首页")
@RequestMapping("/api/applet")
public class AppletMemberController extends ApiBaseAction {
    @Autowired
    private IUmsMemberService memberService;
    @Autowired
    private ISmsHomeAdvertiseService advertiseService;
    @Autowired
    private ISmsCouponService couponService;
    @Autowired
    private IPmsProductAttributeCategoryService productAttributeCategoryService;
    @Autowired
    private IPmsSmallNaviconCategoryService smallNaviconCategoryService;
    @Autowired
    private ISmsFlashPromotionService smsFlashPromotionService;
    @Autowired
    private  ISmsFlashPromotionProductRelationService smsFlashPromotionProductRelationService;
    @Autowired
    ISmsFlashPromotionSessionService smsFlashPromotionSessionService;
    @Autowired
    private IPmsProductService pmsProductService;
    @Autowired
    private ISmsHomeNewProductService smsHomeNewProductService;
    @Autowired
    private ISmsHomeRecommendProductService smsHomeRecommendProductService;
    @Autowired
    private IPmsProductCategoryService pmsProductCategoryService;

    @Autowired
    private ICmsSubjectService subjectService;
    @Autowired
    private IOmsOrderService orderService;

    @Autowired
    private RedisService redisService;
    @Resource
    private  SmsFlashPromotionSessionMapper smsFlashPromotionSessionMapper;
    @Resource
    private SmsHomeRecommendProductMapper smsHomeRecommendProductMapper;
    @Resource
    private SmsHomeNewProductMapper smsHomeNewProductMapper;




    @Resource
    private ISmsRedPacketService redPacketService;

    @Resource
    private ISmsUserRedPacketService userRedPacketService;


    @IgnoreAuth
    @ApiOperation("注册")
    @SysLog(MODULE = "applet", REMARK = "小程序注册")
    @PostMapping("login_by_weixin")
    public Object loginByWeixin(HttpServletRequest req) {
        return memberService.loginByWeixin(req);

    }

    /**
     * 小程序主页
     *
     * @param
     * @return
     */
    //首页获取，轮播图，分类，团购商品，分类商品，秒杀商品
    @IgnoreAuth
    @SysLog(MODULE = "applet", REMARK = "小程序首页")
    @ApiOperation("小程序首页")
    @GetMapping("/index")
    public Object index() {

        List<TArticleDO> model_list = new ArrayList<>();
        IndexData data = new IndexData();
        try {
            TArticleDO a = new TArticleDO("banner");
            TArticleDO a1 = new TArticleDO("search");
            TArticleDO a2 = new TArticleDO("nav");
            TArticleDO a3 = new TArticleDO("cat");
            TArticleDO a4 = new TArticleDO("coupon");
            TArticleDO a5 = new TArticleDO("topic");
            TArticleDO a6 = new TArticleDO("redPacket");
            TArticleDO b2 = new TArticleDO("block", "3");
            TArticleDO b1 = new TArticleDO("block", "4");
            TArticleDO b3 = new TArticleDO("block", "5");
            model_list.add(a);
            model_list.add(a1);
            model_list.add(a2);
            model_list.add(a3);
            model_list.add(a4);
            model_list.add(a5);
            model_list.add(a6);
            model_list.add(b1);
            model_list.add(b2);
            model_list.add(b3);
            //获取轮播图
            List<SmsHomeAdvertise> bannerList = null;
            SmsHomeAdvertise queryT = new SmsHomeAdvertise();
            String bannerJson = redisService.get(RedisKey.appletBannerKey + "2");
            if (bannerJson != null) {
                bannerList = JsonUtil.jsonToList(bannerJson, SmsHomeAdvertise.class);
            }
            if(bannerJson==null||bannerList.size()<=0){
                queryT.setType(2);
                bannerList = advertiseService.list(new QueryWrapper<>(queryT));
                redisService.set(RedisKey.appletBannerKey + "2", JsonUtil.objectToJson(bannerList));
                redisService.expire(RedisKey.appletBannerKey + "2", 24 * 60 * 60);
            }
            //获取轮播结束
            //获取分类
            List<PmsSmallNaviconCategory> nav_icon_list = null;
            PmsSmallNaviconCategory queryT_small = new PmsSmallNaviconCategory();
            String navIconJson = redisService.get(RedisKey.appletNavIconKey);
            if(navIconJson !=null){
                nav_icon_list =JsonUtil.jsonToList(navIconJson, PmsSmallNaviconCategory.class);
            }
            if(navIconJson==null||nav_icon_list.size()<=0){
                nav_icon_list = smallNaviconCategoryService.list(new QueryWrapper<>(queryT_small));
                redisService.set(RedisKey.appletNavIconKey,JsonUtil.objectToJson(nav_icon_list));
                redisService.expire(RedisKey.appletNavIconKey, 24 * 60 * 60);
            }
            //获取分类结束
            //获取秒杀活动商品
            //查询当前在线秒杀活动
            List<SmsFlashPromotionProducts> sms_flash_promotionProducts_List = null;
            String smsFlashPromotionProductJson = redisService.get(RedisKey.appletsmsFlashPromotionProductKey);
            if(smsFlashPromotionProductJson!=null){
                sms_flash_promotionProducts_List = JsonUtil.jsonToList(smsFlashPromotionProductJson,SmsFlashPromotionProducts.class);
            }
            if(smsFlashPromotionProductJson==null||sms_flash_promotionProducts_List.size()<=0){
                SmsFlashPromotion queryS = new SmsFlashPromotion();
                queryS.setIsIndex(1);
                SmsFlashPromotion indexFlashPromotion = smsFlashPromotionService.getOne(new QueryWrapper<>(queryS));
                Long flashPromotionId = indexFlashPromotion.getId();
                //首页秒杀活动数据

                //根据时间计算当前点档
                Date now = new Date();
                String formatNow = DateFormatUtils.format(now,"HH:mm:ss");

                SmsFlashSessionInfo smsFlashSessionInfo = smsFlashPromotionSessionMapper.getCurrentDang(formatNow);
                if(smsFlashSessionInfo!=null){
                    Long smsFlashSessionId = smsFlashSessionInfo.getId();
                    SmsFlashPromotionProductRelation querySMP = new SmsFlashPromotionProductRelation();
                    querySMP.setFlashPromotionId(flashPromotionId);
                    querySMP.setFlashPromotionSessionId(smsFlashSessionId);
                    List<SmsFlashPromotionProductRelation> smsFlashPromotionProductRelationlist = smsFlashPromotionProductRelationService.list(new QueryWrapper<>(querySMP));
                    for (SmsFlashPromotionProductRelation item:smsFlashPromotionProductRelationlist) {
                        PmsProduct product = pmsProductService.getById(item.getProductId());
                        SmsFlashPromotionProducts smsFlashPromotionProduct = new SmsFlashPromotionProducts();
                        smsFlashPromotionProduct.setId(item.getId());
                        smsFlashPromotionProduct.setFlashPromotionCount(item.getFlashPromotionCount());
                        smsFlashPromotionProduct.setFlashPromotionLimit(item.getFlashPromotionLimit());
                        smsFlashPromotionProduct.setFlashPromotionPrice(item.getFlashPromotionPrice());
                        smsFlashPromotionProduct.setProduct(product);
                        sms_flash_promotionProducts_List.add(smsFlashPromotionProduct);
                    }
                    redisService.set(RedisKey.appletsmsFlashPromotionProductKey,JsonUtil.objectToJson(sms_flash_promotionProducts_List));
                    redisService.expire(RedisKey.appletsmsFlashPromotionProductKey, 24 * 60 * 60);
                }
            }
            //获取秒杀活动结束
            //获取首页分类商品列表
            List<CateProduct> cateProductList = null;
            List<CateProduct> temp = new ArrayList<>();
            String cateProductJson = redisService.get(RedisKey.appletCateProductsKey);
            if(cateProductJson!=null){
                cateProductList = JsonUtil.jsonToList(cateProductJson,CateProduct.class);
            }
            if(cateProductJson==null||cateProductList.size()<=0){
                PmsProductCategory queryP = new PmsProductCategory();
                queryP.setIndexStatus(1);
                List<PmsProductCategory> pmsProductCategoryList = pmsProductCategoryService.list(new QueryWrapper<>(queryP));
                if(pmsProductCategoryList.size()>0){
                for (PmsProductCategory item :pmsProductCategoryList)
                {
                    PmsProduct queryProduct = new PmsProduct();
                    queryProduct.setProductCategoryId(item.getId());
                    List<PmsProduct> pmsProductList = pmsProductService.list(new QueryWrapper<>(queryProduct));//商品列表
                    CateProduct cateProduct = new CateProduct();
                    cateProduct.setCategoryId(item.getId());
                    cateProduct.setCategoryName(item.getName());
                    cateProduct.setCategoryImage(item.getIcon());
                    cateProduct.setPmsProductList(pmsProductList);
                    //存入分类+商品对象vo
                    temp.add(cateProduct);
                }
                cateProductList = temp;
                redisService.set(RedisKey.appletCateProductsKey,JsonUtil.objectToJson(cateProductList));
                redisService.expire(RedisKey.appletCateProductsKey, 24 * 60 * 60);
                }
            }

            //获取首页分类商品列表结束

            //获取热门商品列表
            List<HomeProductAttr> hot_productList = null;
            String hotProductJson = redisService.get(RedisKey.appletHotProductsKey);
            if(hotProductJson!=null){
                hot_productList = JsonUtil.jsonToList(hotProductJson,HomeProductAttr.class);
            }
            if(hotProductJson==null||hot_productList.size()<=0){
                hot_productList = smsHomeRecommendProductMapper.queryList();
                if(hot_productList!=null){
                redisService.set(RedisKey.appletHotProductsKey,JsonUtil.objectToJson(hot_productList));
                redisService.expire(RedisKey.appletHotProductsKey, 24 * 60 * 60);
                }
            }

            //获取热门商品列表结束
            //获取首页新品列表
            List<HomeProductAttr> new_productList = null;
            String newProductJson = redisService.get(RedisKey.appletNewProductsKey);
            if(newProductJson!=null){
                new_productList = JsonUtil.jsonToList(newProductJson,HomeProductAttr.class);
            }
            if(newProductJson==null||new_productList.size()<=0){
                new_productList = smsHomeNewProductMapper.queryList();
                if(new_productList!=null) {
                    redisService.set(RedisKey.appletNewProductsKey, JsonUtil.objectToJson(new_productList));
                    redisService.expire(RedisKey.appletNewProductsKey, 24 * 60 * 60);
                }
            }
            //获取首页新品列表结束
            //品牌推荐列表开始

            //品牌推荐列表结束
            //获取优惠券
            List<SmsCoupon> couponList = new ArrayList<>();
            couponList = couponService.selectNotRecive();
            //获取优惠券结束
            List<PmsProductAttributeCategory> productAttributeCategoryList = null;
            String catJson = redisService.get(RedisKey.appletCategoryKey);
            if (catJson != null) {
                productAttributeCategoryList = JsonUtil.jsonToList(catJson, PmsProductAttributeCategory.class);
            } else {
                productAttributeCategoryList = productAttributeCategoryService.list(new QueryWrapper<>());
                for (PmsProductAttributeCategory gt : productAttributeCategoryList) {
                    PmsProduct productQueryParam = new PmsProduct();
                    productQueryParam.setProductAttributeCategoryId(gt.getId());
                    productQueryParam.setPublishStatus(1);
                    productQueryParam.setVerifyStatus(1);
                    gt.setGoodsList(pmsProductService.list(new QueryWrapper<>(productQueryParam)));
                }
                redisService.set(RedisKey.appletCategoryKey, JsonUtil.objectToJson(productAttributeCategoryList));
                redisService.expire(RedisKey.appletCategoryKey, 24 * 60 * 60);
            }
            List<CmsSubject> subjectList = subjectService.list(new QueryWrapper<>());
            data.setSubjectList(subjectList);
            data.setCat_goods_cols(2);
            data.setCat_list(productAttributeCategoryList);
            data.setNav_icon_list(nav_icon_list);
            data.setBanner_list(bannerList);
            data.setCoupon_list(couponList);
            data.setSms_flash_promotionProducts_List(sms_flash_promotionProducts_List);
            data.setCate_products(cateProductList);
            data.setHot_products(hot_productList);
            data.setNew_products(new_productList);
            data.setModule_list(model_list);
//            List<SmsRedPacket> redPacketList = redPacketService.list(new QueryWrapper<>());
//            SmsUserRedPacket userRedPacket = new SmsUserRedPacket();
//            userRedPacket.setUserId(UserUtils.getCurrentMember().getId());
//            List<SmsUserRedPacket> list = userRedPacketService.list(new QueryWrapper<>(userRedPacket));
//            for(SmsRedPacket vo : redPacketList){
//                if (list!=null && list.size()>0){
//                    for (SmsUserRedPacket vo1 : list){
//                        if(vo.getId().equals(vo1.getRedPacketId())){
//                            vo.setStatus(1);
//                            vo.setReciveAmount(vo1.getAmount());
//                            break;
//                        }
//                    }
//                }
//            }
//            data.setRedPacketList(redPacketList);
            return new CommonResult().success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return new CommonResult().failed();
        }

    }

    @IgnoreAuth
    @ApiOperation("小程序用户详情")
    @SysLog(MODULE = "applet", REMARK = "小程序用户详情")
    @GetMapping("/user")
    public Object user() {
        UmsMember umsMember = UserUtils.getCurrentMember();
        if (umsMember != null && umsMember.getId() != null) {
            OmsOrder param = new OmsOrder();
            param.setMemberId(umsMember.getId());
            List<OmsOrder> list = orderService.list(new QueryWrapper<>(param));
            int status0 = 0;
            int status1 = 0;
            int status2 = 0;
            int status3 = 0;
            int status4 = 0;
            int status5 = 0;
            OrderStatusCount count = new OrderStatusCount();
            for (OmsOrder consult : list) {
                if (consult.getStatus() == 0) {
                    status0++;
                }
                if (consult.getStatus() == 1) {
                    status1++;
                }
                if (consult.getStatus() == 2) {
                    status2++;
                }
                if (consult.getStatus() == 3) {
                    status2++;
                }
                if (consult.getStatus() == 4) {
                    status4++;
                }
                if (consult.getStatus() == 5) {
                    status5++;
                }
            }
            count.setStatus0(status0);
            count.setStatus1(status1);
            count.setStatus2(status2);
            count.setStatus3(status3);
            count.setStatus4(status4);
            count.setStatus5(status5);
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("user", umsMember);
            objectMap.put("count", count);
            return new CommonResult().success(objectMap);
        }
        return new CommonResult().failed();

    }
}
