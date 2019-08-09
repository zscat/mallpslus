package com.zscat.mallplus.pms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zscat.mallplus.cms.service.ICmsPrefrenceAreaProductRelationService;
import com.zscat.mallplus.cms.service.ICmsSubjectProductRelationService;
import com.zscat.mallplus.pms.entity.*;
import com.zscat.mallplus.pms.mapper.*;
import com.zscat.mallplus.pms.service.*;
import com.zscat.mallplus.pms.vo.GoodsDetailResult;
import com.zscat.mallplus.pms.vo.PmsProductAndGroup;
import com.zscat.mallplus.pms.vo.PmsProductParam;
import com.zscat.mallplus.pms.vo.PmsProductResult;
import com.zscat.mallplus.sms.entity.*;
import com.zscat.mallplus.sms.mapper.SmsGroupMapper;
import com.zscat.mallplus.sms.mapper.SmsGroupMemberMapper;
import com.zscat.mallplus.sms.service.ISmsHomeBrandService;
import com.zscat.mallplus.sms.service.ISmsHomeNewProductService;
import com.zscat.mallplus.sms.service.ISmsHomeRecommendProductService;
import com.zscat.mallplus.ums.service.RedisService;
import com.zscat.mallplus.util.DateUtils;
import com.zscat.mallplus.util.GoodsUtils;
import com.zscat.mallplus.util.JsonUtils;
import com.zscat.mallplus.vo.Rediskey;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-04-19
 */
@Service
public class PmsProductServiceImpl extends ServiceImpl<PmsProductMapper, PmsProduct> implements IPmsProductService {

    @Resource
    private IPmsBrandService brandService;
    @Resource
    private ISmsHomeBrandService homeBrandService;
    @Resource
    private ISmsHomeNewProductService homeNewProductService;
    @Resource
    private ISmsHomeRecommendProductService homeRecommendProductService;
    @Resource
    private PmsProductMapper productMapper;
    @Resource
    private IPmsMemberPriceService memberPriceDao;
    @Resource
    private PmsMemberPriceMapper memberPriceMapper;
    @Resource
    private IPmsProductLadderService productLadderDao;
    @Resource
    private PmsProductLadderMapper productLadderMapper;
    @Resource
    private IPmsProductFullReductionService productFullReductionDao;
    @Resource
    private PmsProductFullReductionMapper productFullReductionMapper;
    @Resource
    private IPmsSkuStockService skuStockDao;
    @Resource
    private PmsSkuStockMapper skuStockMapper;
    @Resource
    private IPmsProductAttributeValueService productAttributeValueDao;
    @Resource
    private PmsProductAttributeValueMapper productAttributeValueMapper;
    @Resource
    private ICmsSubjectProductRelationService subjectProductRelationDao;
    @Resource
    private CmsSubjectProductRelationMapper subjectProductRelationMapper;
    @Resource
    private ICmsPrefrenceAreaProductRelationService prefrenceAreaProductRelationDao;
    @Resource
    private CmsPrefrenceAreaProductRelationMapper prefrenceAreaProductRelationMapper;

    @Resource
    private PmsProductVertifyRecordMapper productVertifyRecordDao;

    @Resource
    private PmsProductVertifyRecordMapper productVertifyRecordMapper;

    @Resource
    private SmsGroupMapper groupMapper;
    @Resource
    private SmsGroupMemberMapper groupMemberMapper;
    @Resource
    private RedisService redisService;

    @Override
    public PmsProductAndGroup getProductAndGroup(Long id) {
        PmsProduct goods = productMapper.selectById(id);
        PmsProductAndGroup vo = new PmsProductAndGroup();
        try {
            BeanUtils.copyProperties(goods, vo);
            SmsGroup queryG = new SmsGroup();
            queryG.setGoodsId(id);
            SmsGroup group = groupMapper.selectOne(new QueryWrapper<>(queryG));
            SmsGroupMember newG = new SmsGroupMember();
            newG.setGoodsId(id);
            List<SmsGroupMember> list = groupMemberMapper.selectList(new QueryWrapper<>(newG));
            if (group != null) {
                Map<String, List<SmsGroupMember>> map = groupMemberByMainId(list, group);
                vo.setMap(map);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }

    /**
     * 按照异常批次号对已开单数据进行分组
     *
     * @param billingList
     * @return
     * @throws Exception
     */
    private Map<String, List<SmsGroupMember>> groupMemberByMainId(List<SmsGroupMember> billingList, SmsGroup group) throws Exception {
        Map<String, List<SmsGroupMember>> resultMap = new HashMap<String, List<SmsGroupMember>>();
        Map<String, List<SmsGroupMember>> map = new HashMap<String, List<SmsGroupMember>>();
        try {
            List<Long> ids = new ArrayList<>();
            for (SmsGroupMember tmExcpNew : billingList) {
                if (tmExcpNew.getMemberId().equals(tmExcpNew.getMainId())) {
                    Date cr = tmExcpNew.getCreateTime();
                    Long nowT = System.currentTimeMillis();
                    Date endTime = DateUtils.convertStringToDate(DateUtils.addHours(cr, group.getHours()), "yyyy-MM-dd HH:mm:ss");
                    if (nowT <= endTime.getTime()) {
                        ids.add(tmExcpNew.getMainId());
                    }
                }
                if (resultMap.containsKey(tmExcpNew.getMainId() + "")) {//map中异常批次已存在，将该数据存放到同一个key（key存放的是异常批次）的map中
                    resultMap.get(tmExcpNew.getMainId() + "").add(tmExcpNew);
                } else {//map中不存在，新建key，用来存放数据
                    List<SmsGroupMember> tmpList = new ArrayList<SmsGroupMember>();
                    tmpList.add(tmExcpNew);
                    resultMap.put(tmExcpNew.getMainId() + "", tmpList);
                }
            }
            for (Long id : ids) {
                if (resultMap.get(id + "") != null) {
                    map.put(id + "", resultMap.get(id + ""));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("按照异常批次号对已开单数据进行分组时出现异常", e);
        }

        return map;
    }

    @Override
    public PmsProduct getUpdateInfo(Long id) {
        return  productMapper.selectById(id);
    }

    @Override
    public Object initGoodsRedis() {
        List<PmsProduct> list = productMapper.selectList(new QueryWrapper<>());
        for (PmsProduct goods : list) {
            PmsProductParam param = new PmsProductParam();
            param.setGoods(goods);

            List<PmsProductLadder> productLadderList = productLadderMapper.selectList(new QueryWrapper<PmsProductLadder>().eq("product_id", goods.getId()));

            List<PmsProductFullReduction> productFullReductionList = productFullReductionMapper.selectList(new QueryWrapper<PmsProductFullReduction>().eq("product_id", goods.getId()));

            List<PmsMemberPrice> memberPriceList = memberPriceMapper.selectList(new QueryWrapper<PmsMemberPrice>().eq("product_id", goods.getId()));

            List<PmsSkuStock> skuStockList = skuStockMapper.selectList(new QueryWrapper<PmsSkuStock>().eq("product_id", goods.getId()));

            List<PmsProductAttributeValue> productAttributeValueList = productAttributeValueMapper.selectList(new QueryWrapper<PmsProductAttributeValue>().eq("product_id", goods.getId()));

            List<CmsSubjectProductRelation> subjectProductRelationList = subjectProductRelationMapper.selectList(new QueryWrapper<CmsSubjectProductRelation>().eq("product_id", goods.getId()));

            List<CmsPrefrenceAreaProductRelation> prefrenceAreaProductRelationList = prefrenceAreaProductRelationMapper.selectList(new QueryWrapper<CmsPrefrenceAreaProductRelation>().eq("product_id", goods.getId()));

            param.setMemberPriceList(memberPriceList);
            param.setPrefrenceAreaProductRelationList(prefrenceAreaProductRelationList);
            param.setProductAttributeValueList(productAttributeValueList);
            param.setProductFullReductionList(productFullReductionList);
            param.setProductLadderList(productLadderList);
            param.setSkuStockList(skuStockList);
            param.setSubjectProductRelationList(subjectProductRelationList);
            redisService.set(String.format(Rediskey.GOODSDETAIL, goods.getId()), JsonUtils.objectToJson(param));
        }
        return 1;
    }

    @Override
    public GoodsDetailResult getGoodsRedisById(Long id) {
        PmsProduct goods = productMapper.selectById(id);

        GoodsDetailResult param = new GoodsDetailResult();
        param.setGoods(goods);

      /*  List<PmsProductLadder> productLadderList = productLadderMapper.selectList(new QueryWrapper<PmsProductLadder>().eq("product_id", goods.getId()));

        List<PmsProductFullReduction> productFullReductionList = productFullReductionMapper.selectList(new QueryWrapper<PmsProductFullReduction>().eq("product_id", goods.getId()));

        List<PmsMemberPrice> memberPriceList = memberPriceMapper.selectList(new QueryWrapper<PmsMemberPrice>().eq("product_id", goods.getId()));
  param.setMemberPriceList(memberPriceList);
        param.setProductFullReductionList(productFullReductionList);
        param.setProductLadderList(productLadderList);
*/
        List<PmsSkuStock> skuStockList = skuStockMapper.selectList(new QueryWrapper<PmsSkuStock>().eq("product_id", goods.getId()));

        List<PmsProductAttributeValue> productAttributeValueList = productAttributeValueMapper.selectList(new QueryWrapper<PmsProductAttributeValue>().eq("product_id", goods.getId()).eq("type",1));

        List<CmsSubjectProductRelation> subjectProductRelationList = subjectProductRelationMapper.selectList(new QueryWrapper<CmsSubjectProductRelation>().eq("product_id", goods.getId()));

        List<CmsPrefrenceAreaProductRelation> prefrenceAreaProductRelationList = prefrenceAreaProductRelationMapper.selectList(new QueryWrapper<CmsPrefrenceAreaProductRelation>().eq("product_id", goods.getId()));

        List<PmsProductAttributeValue> productCanShuValueList = productAttributeValueMapper.selectList(new QueryWrapper<PmsProductAttributeValue>().eq("product_id", goods.getId()).eq("type",2));
        param.setProductCanShuValueList(productCanShuValueList);

        param.setPrefrenceAreaProductRelationList(prefrenceAreaProductRelationList);
        param.setProductAttributeValueList(productAttributeValueList);

        param.setSkuStockList(skuStockList);
        param.setSubjectProductRelationList(subjectProductRelationList);

        List<PmsProduct> typeGoodsList = productMapper.selectList(new QueryWrapper<PmsProduct>().eq("product_attribute_category_id",goods.getProductAttributeCategoryId()));
        param.setTypeGoodsList(GoodsUtils.sampleGoodsList(typeGoodsList.subList(0,typeGoodsList.size()>8?8:typeGoodsList.size())));
        redisService.set(String.format(Rediskey.GOODSDETAIL, goods.getId()), JsonUtils.objectToJson(param));

        return param;
    }
    @Override
    public List<PmsBrand> getRecommendBrandList(int pageNum, int pageSize) {
        List<SmsHomeBrand> brands = homeBrandService.list(new QueryWrapper<>());
        List<Long> ids = brands.stream()
                .map(SmsHomeBrand::getId)
                .collect(Collectors.toList());
        return (List<PmsBrand>) brandService.listByIds(ids);

    }
    @Override
    public List<PmsProduct> getNewProductList(int pageNum, int pageSize) {
        List<SmsHomeNewProduct> brands = homeNewProductService.list(new QueryWrapper<>());
        List<Long> ids = brands.stream()
                .map(SmsHomeNewProduct::getId)
                .collect(Collectors.toList());
        return (List<PmsProduct>) productMapper.selectBatchIds(ids);
    }
    @Override
    public List<PmsProduct> getHotProductList(int pageNum, int pageSize) {
        List<SmsHomeRecommendProduct> brands = homeRecommendProductService.list(new QueryWrapper<>());
        List<Long> ids = brands.stream()
                .map(SmsHomeRecommendProduct::getId)
                .collect(Collectors.toList());
        return (List<PmsProduct>)productMapper.selectBatchIds(ids);
    }

    @Override
    public  Integer countGoodsByToday(Long id){
        return productMapper.countGoodsByToday(id);
    }
}
