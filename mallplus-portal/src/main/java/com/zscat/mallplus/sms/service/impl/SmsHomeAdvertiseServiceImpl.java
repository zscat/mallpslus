package com.zscat.mallplus.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zscat.mallplus.cms.entity.CmsSubject;
import com.zscat.mallplus.cms.entity.CmsSubjectComment;
import com.zscat.mallplus.cms.service.ICmsSubjectCategoryService;
import com.zscat.mallplus.cms.service.ICmsSubjectCommentService;
import com.zscat.mallplus.cms.service.ICmsSubjectService;
import com.zscat.mallplus.oms.service.IOmsOrderService;
import com.zscat.mallplus.oms.vo.HomeContentResult;
import com.zscat.mallplus.pms.entity.PmsBrand;
import com.zscat.mallplus.pms.entity.PmsProduct;
import com.zscat.mallplus.pms.entity.PmsProductAttributeCategory;
import com.zscat.mallplus.pms.service.IPmsBrandService;
import com.zscat.mallplus.pms.service.IPmsProductAttributeCategoryService;
import com.zscat.mallplus.pms.service.IPmsProductCategoryService;
import com.zscat.mallplus.pms.service.IPmsProductService;
import com.zscat.mallplus.pms.vo.SamplePmsProduct;
import com.zscat.mallplus.sms.entity.*;
import com.zscat.mallplus.sms.mapper.*;
import com.zscat.mallplus.sms.service.*;
import com.zscat.mallplus.sms.vo.HomeFlashPromotion;
import com.zscat.mallplus.sms.vo.HomeProductAttr;
import com.zscat.mallplus.sms.vo.SmsFlashSessionInfo;
import com.zscat.mallplus.ums.service.IUmsMemberLevelService;
import com.zscat.mallplus.ums.service.IUmsMemberService;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 首页轮播广告表 服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-04-19
 */
@Service
public class SmsHomeAdvertiseServiceImpl extends ServiceImpl<SmsHomeAdvertiseMapper, SmsHomeAdvertise> implements ISmsHomeAdvertiseService {
    @Autowired
    private IUmsMemberService memberService;
    @Autowired
    private ISmsHomeAdvertiseService advertiseService;
    @Autowired
    private IOmsOrderService orderService;
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
    private ISmsHomeBrandService homeBrandService;
    @Resource
    private ISmsHomeNewProductService homeNewProductService;
    @Resource
    private ISmsHomeRecommendProductService homeRecommendProductService;
    @Resource
    private ISmsHomeRecommendSubjectService homeRecommendSubjectService;
    @Resource
    private SmsFlashPromotionSessionMapper smsFlashPromotionSessionMapper;
    @Resource
    private SmsHomeRecommendProductMapper smsHomeRecommendProductMapper;
    @Resource
    private SmsHomeNewProductMapper smsHomeNewProductMapper;
    @Autowired
    private ISmsFlashPromotionService smsFlashPromotionService;
    @Autowired
    private ISmsFlashPromotionProductRelationService smsFlashPromotionProductRelationService;
    @Resource
    private ICmsSubjectCategoryService subjectCategoryService;
    @Resource
    private ICmsSubjectService subjectService;
    @Resource
    private ICmsSubjectCommentService commentService;
    @Resource
    private IPmsBrandService brandService;
    @Resource
    private SmsGroupMemberMapper groupMemberMapper;
    @Override
    public HomeContentResult singelContent() {
        HomeContentResult result = new HomeContentResult();
        //获取首页广告
        result.setAdvertiseList(getHomeAdvertiseList());
        //获取推荐品牌
        result.setBrandList(this.getRecommendBrandList(0, 4));
        //获取秒杀信息
        result.setHomeFlashPromotion(getHomeFlashPromotion());
        //获取新品推荐
        result.setNewProductList(sampleGoodsList(this.getNewProductList(0, 4)));
        //获取人气推荐
        result.setHotProductList(sampleGoodsList(this.getHotProductList(0, 4)));
        //获取推荐专题
        result.setSubjectList(this.getRecommendSubjectList(0, 4));
        List<PmsProductAttributeCategory> productAttributeCategoryList = getPmsProductAttributeCategories();
        result.setCat_list(productAttributeCategoryList);
        return result;
    }
    @Override
    public List<PmsProductAttributeCategory> getPmsProductAttributeCategories() {
        List<PmsProductAttributeCategory> productAttributeCategoryList = productAttributeCategoryService.list(new QueryWrapper<>());

        for (PmsProductAttributeCategory gt : productAttributeCategoryList) {
            PmsProduct productQueryParam = new PmsProduct();
            productQueryParam.setProductAttributeCategoryId(gt.getId());
            productQueryParam.setPublishStatus(1);
            productQueryParam.setVerifyStatus(1);
            IPage<PmsProduct> goodsList = pmsProductService.page(new Page<PmsProduct>(0, 8),new QueryWrapper<>(productQueryParam));

            if (goodsList!=null&& goodsList.getRecords()!=null && goodsList.getRecords().size()>0){
                gt.setGoodsList(sampleGoodsList(goodsList.getRecords()));
            }else{
                gt.setGoodsList(new ArrayList<>());
            }

        }
        return productAttributeCategoryList;
    }

    private HomeFlashPromotion getHomeFlashPromotion() {
        HomeFlashPromotion homeFlashPromotion = null;
        HomeFlashPromotion tempsmsFlashList = new HomeFlashPromotion();
        SmsFlashPromotion queryS = new SmsFlashPromotion();
        queryS.setIsIndex(1);
        SmsFlashPromotion indexFlashPromotion = smsFlashPromotionService.getOne(new QueryWrapper<>(queryS));
        Long flashPromotionId = 0L;
        //数据库中有当前秒杀活动时赋值
        if (indexFlashPromotion != null) {
            flashPromotionId = indexFlashPromotion.getId();
        }
        //首页秒杀活动数据

        //根据时间计算当前点档
        Date now = new Date();
        String formatNow = DateFormatUtils.format(now, "HH:mm:ss");

        SmsFlashSessionInfo smsFlashSessionInfo = smsFlashPromotionSessionMapper.getCurrentDang(formatNow);
        if (smsFlashSessionInfo != null && flashPromotionId != 0L) {//当前时间有秒杀档，并且有秒杀活动时，获取数据
            Long smsFlashSessionId = smsFlashSessionInfo.getId();
            //秒杀活动点档信息存储
            tempsmsFlashList.setId(smsFlashSessionId);
            tempsmsFlashList.setFlashName(smsFlashSessionInfo.getName());
            tempsmsFlashList.setStartTime(smsFlashSessionInfo.getStartTime());
            tempsmsFlashList.setEndTime(smsFlashSessionInfo.getEndTime());
            SmsFlashPromotionProductRelation querySMP = new SmsFlashPromotionProductRelation();
            querySMP.setFlashPromotionId(flashPromotionId);
            querySMP.setFlashPromotionSessionId(smsFlashSessionId);
            List<SmsFlashPromotionProductRelation> smsFlashPromotionProductRelationlist = smsFlashPromotionProductRelationService.list(new QueryWrapper<>(querySMP));
            List<HomeProductAttr> productAttrs = new ArrayList<>();
            for (SmsFlashPromotionProductRelation item : smsFlashPromotionProductRelationlist) {
                PmsProduct tempproduct = pmsProductService.getById(item.getProductId());
                if (tempproduct!=null){
                    HomeProductAttr product = new HomeProductAttr();
                    product.setProductId(tempproduct.getId());
                    product.setProductImg(tempproduct.getPic());
                    product.setProductName(tempproduct.getName());
                    product.setProductPrice(tempproduct.getPromotionPrice() != null ? tempproduct.getPromotionPrice() : BigDecimal.ZERO);
                    productAttrs.add(product);
                }
            }
            tempsmsFlashList.setProductList(productAttrs);
            homeFlashPromotion = tempsmsFlashList;
//                    redisService.set(Rediskey.appletsmsFlashPromotionProductKey,JsonUtil.objectToJson(homeFlashPromotion));
//                    redisService.expire(Rediskey.appletsmsFlashPromotionProductKey, 24 * 60 * 60);
        }
        return homeFlashPromotion;
    }


    @Override
    public List<PmsBrand> getRecommendBrandList(int pageNum, int pageSize) {
        List<SmsHomeBrand> brands = homeBrandService.list(new QueryWrapper<>());
        if(brands==null || brands.size()==0){
            return new ArrayList<>();
        }
        List<Long> ids = brands.stream()
                .map(SmsHomeBrand::getBrandId)
                .collect(Collectors.toList());
        return (List<PmsBrand>) brandService.listByIds(ids);

    }
    @Override
    public List<PmsProduct> getNewProductList(int pageNum, int pageSize) {
        PmsProduct query = new PmsProduct();
        query.setPublishStatus(1);
        query.setVerifyStatus(1);
       return  pmsProductService.page(new Page<PmsProduct>(pageNum, pageSize), new QueryWrapper<>(query).orderByDesc("create_time")).getRecords();

       /* List<SmsHomeNewProduct> brands = homeNewProductService.list(new QueryWrapper<>());
        List<Long> ids = brands.stream()
                .map(SmsHomeNewProduct::getProductId)
                .collect(Collectors.toList());
        return (List<PmsProduct>) pmsProductService.listByIds(ids);*/
    }
    public List<SamplePmsProduct> sampleGoodsList(List<PmsProduct> list){
        List<SamplePmsProduct> products= new ArrayList<>();
        for (PmsProduct product:list){
            SamplePmsProduct en =new SamplePmsProduct();
            BeanUtils.copyProperties(product,en);
            products.add(en);
        }
        return products;
    }
    @Override
    public List<PmsProduct> getHotProductList(int pageNum, int pageSize) {
        List<SmsHomeRecommendProduct> brands = homeRecommendProductService.list(new QueryWrapper<>());
        if(brands==null || brands.size()==0){
            return new ArrayList<>();
        }
        List<Long> ids = brands.stream()
                .map(SmsHomeRecommendProduct::getProductId)
                .collect(Collectors.toList());
        return (List<PmsProduct>) pmsProductService.listByIds(ids);
    }
    @Override
    public List<CmsSubject> getRecommendSubjectList(int pageNum, int pageSize) {
        List<SmsHomeRecommendSubject> brands = homeRecommendSubjectService.list(new QueryWrapper<>());
        if(brands==null || brands.size()==0){
            return new ArrayList<>();
        }

        List<Long> ids = brands.stream()
                .map(SmsHomeRecommendSubject::getSubjectId)
                .collect(Collectors.toList());
        return (List<CmsSubject>) subjectService.listByIds(ids);
    }

    @Override
    public List<SmsHomeAdvertise> getHomeAdvertiseList() {
        SmsHomeAdvertise advertise = new SmsHomeAdvertise();
        advertise.setStatus(1);
        return advertiseService.list(new QueryWrapper<>(advertise));
    }


}
