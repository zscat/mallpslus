package com.zscat.mallplus.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zscat.mallplus.oms.entity.OmsCartItem;
import com.zscat.mallplus.oms.vo.CartMarkingVo;
import com.zscat.mallplus.pms.entity.PmsProduct;
import com.zscat.mallplus.pms.mapper.PmsProductMapper;
import com.zscat.mallplus.sms.entity.SmsBasicMarking;
import com.zscat.mallplus.sms.mapper.SmsBasicMarkingMapper;
import com.zscat.mallplus.sms.service.ISmsBasicMarkingService;
import com.zscat.mallplus.sms.vo.BasicRuls;
import com.zscat.mallplus.sms.vo.BeanKv;
import com.zscat.mallplus.util.JsonUtils;
import com.zscat.mallplus.utils.ValidatorUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-07-07
 */
@Service
public class SmsBasicMarkingServiceImpl extends ServiceImpl<SmsBasicMarkingMapper, SmsBasicMarking> implements ISmsBasicMarkingService {

    @Resource
    private SmsBasicMarkingMapper markingMapper;
    @Resource
    private PmsProductMapper goodsMapper;
    /**
     *   * 1 有效2 无效
     * @param id
     * @param status
     * @return
     */
    @Transactional
    @Override
    public int updateStatus(Long id, Integer status,Integer bigType) {
        SmsBasicMarking marking = new SmsBasicMarking();
        if (status==1){
            marking.setId(id);
            marking.setStatus(0);
            markingMapper.updateById(marking);
        }else {
            marking.setStatus(0);
            markingMapper.update(marking,new QueryWrapper<SmsBasicMarking>().eq("big_type",bigType));
            marking.setId(id);
            marking.setStatus(1);
            markingMapper.updateById(marking);
        }
        return 0;
    }

    /**
     *  查询单个商品的优惠
     * @param id
     * @return
     */
    @Override
    public List<SmsBasicMarking> matchGoodsMk(Long id) {
        PmsProduct product = goodsMapper.selectById(id);
        List<SmsBasicMarking> list = markingMapper.selectList(new QueryWrapper<SmsBasicMarking>().eq("status",1));
        List<SmsBasicMarking> newList =new ArrayList<>();
        for(SmsBasicMarking m : list){
            if(checkManjian(m)){
                if(m.getBigType()==1){ // 1 满减 2 折扣
                    List<BasicRuls> actrule =  JsonUtils.jsonToList(m.getRules(),BasicRuls.class);

                    if (m.getActiviGoods()==3){ //1 按类别  2 部分商品  3 全部
                        /**
                         * 类型1 消费金额 2 购买件数
                         */
                        if (m.getSmallType() == 1) {
                            Collections.sort(actrule, Comparator.comparing(BasicRuls::getFullPrice).reversed());
                            for (BasicRuls rule : actrule) {
                                if (product.getPrice().compareTo(rule.getFullPrice()) >= 0) {
                                    actrule.clear();
                                    actrule.add(rule);
                                    m.setActrule(actrule);
                                    newList.add(m);
                                    break;
                                }
                            }

                        } else {
                            Collections.sort(actrule, Comparator.comparing(BasicRuls::getFullPrice).reversed());
                            for (BasicRuls rule : actrule) {
                                if (product.getPrice().compareTo(rule.getFullPrice()) >= 0) {
                                    actrule.clear();
                                    actrule.add(rule);
                                    m.setActrule(actrule);
                                    newList.add(m);
                                    break;
                                }
                            }
                        }
                    }else  {
                        List<BeanKv> gList =  JsonUtils.jsonToList(m.getGoodsDs(), BeanKv.class);
                        if (m.getActiviGoods()==1){
                           for(BeanKv k : gList){
                               if(k.getId()==product.getProductCategoryId()){

                               }
                           }
                        }else if (m.getActiviGoods()==2){
                            for(BeanKv k : gList){
                                if(k.getId()==id){

                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    public List<SmsBasicMarking> matchOrderMk(CartMarkingVo vo) {
        BigDecimal totalAmount = new BigDecimal("0");//实付金额
        int count = 0;
        for (OmsCartItem cart : vo.getCartList()) {
            totalAmount = totalAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            count = count + cart.getQuantity();
        }
        List<SmsBasicMarking> list = markingMapper.selectList(new QueryWrapper<SmsBasicMarking>().eq("status",1));
        List<SmsBasicMarking> newList =new ArrayList<>();
        for(SmsBasicMarking m : list){
            if(checkManjian(m)){
                if(m.getBigType()==1){ // 1 满减 2 折扣
                    List<BasicRuls> actrule =  JsonUtils.jsonToList(m.getRules(),BasicRuls.class);

                    if (m.getActiviGoods()==3){ //1 按类别  2 部分商品  3 全部
                        /**
                         * 类型1 消费金额 2 购买件数
                         */
                        if (m.getSmallType() == 1) {
                            Collections.sort(actrule, Comparator.comparing(BasicRuls::getFullPrice).reversed());
                            for (BasicRuls rule : actrule) {

                            }

                        } else {
                            Collections.sort(actrule, Comparator.comparing(BasicRuls::getFullPrice).reversed());
                            for (BasicRuls rule : actrule) {

                            }
                        }
                    }else  {
                        List<BeanKv> gList =  JsonUtils.jsonToList(m.getGoodsDs(), BeanKv.class);
                        if (m.getActiviGoods()==1){
                            if (m.getSmallType() == 1) {
                                Collections.sort(actrule, Comparator.comparing(BasicRuls::getFullPrice).reversed());
                                for (BasicRuls rule : actrule) {

                                }

                            } else {
                                Collections.sort(actrule, Comparator.comparing(BasicRuls::getFullPrice).reversed());
                                for (BasicRuls rule : actrule) {

                                }
                            }
                        }else if (m.getActiviGoods()==2){
                            for(BeanKv k : gList){

                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    /*
         判断是否在高级设置活动范围内
   */
    private boolean checkManjian(SmsBasicMarking manjian)  {
        if (manjian != null) {
            Date da = new Date();
                if (manjian.getStartTime().getTime() <= da.getTime() && manjian.getEndTime().getTime() >= da.getTime()) {
                    return true;
                }
        }
        return false;
    }
}
