package com.zscat.mallplus.component;


import com.zscat.mallplus.cms.entity.CmsSubject;
import com.zscat.mallplus.cms.service.ICmsSubjectService;
import com.zscat.mallplus.oms.service.IOmsOrderService;
import com.zscat.mallplus.pms.entity.PmsProduct;
import com.zscat.mallplus.pms.mapper.PmsProductMapper;
import com.zscat.mallplus.ums.service.impl.RedisUtil;
import com.zscat.mallplus.utils.CommonResult;
import com.zscat.mallplus.vo.Rediskey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * https://github.com/shenzhuan/mallplus on 2018/8/24.
 * 订单超时取消并解锁库存的定时器
 */
@Component
public class OrderTimeOutCancelTask {
    private Logger logger = LoggerFactory.getLogger(OrderTimeOutCancelTask.class);
    @Autowired
    private IOmsOrderService portalOrderService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private PmsProductMapper productMapper;
    @Resource
    private ICmsSubjectService subjectService;
    /**
     * cron表达式：Seconds Minutes Hours DayofMonth Month DayofWeek [Year]
     * 每10分钟扫描一次，扫描设定超时时间之前下的订单，如果没支付则取消该订单
     */
    @Scheduled(cron = "0 0/10 * ? * ?")
    private void cancelTimeOutOrder() {
        CommonResult result = portalOrderService.cancelTimeOutOrder();
        logger.info("取消订单，并根据sku编号释放锁定库存:{}", result);
    }

    /**
     * 文章浏览量
     */
    @Scheduled(cron = "0 0/10 * * * ? ")//每1分钟
    public void SyncNodesAndShips() {
        logger.info("开始保存点赞数 、浏览数SyncNodesAndShips");
        try {
            //先获取这段时间的浏览数
            Map<Object,Object> viewCountItem=redisUtil.hGetAll(Rediskey.ARTICLE_VIEWCOUNT_KEY);
            //然后删除redis里这段时间的浏览数
            redisUtil.delete(Rediskey.ARTICLE_VIEWCOUNT_KEY);
            if(!viewCountItem.isEmpty()){
                for(Object item :viewCountItem.keySet()){
                    String articleKey=item.toString();//viewcount_1
                    String[]  kv=articleKey.split("_");
                    Long articleId=Long.parseLong(kv[1]);
                    Integer viewCount=Integer.parseInt(viewCountItem.get(articleKey).toString());
                    CmsSubject subject = subjectService.getById(articleId);
                    subject.setId(articleId);
                    subject.setReadCount(subject.getReadCount()+viewCount);
                    logger.info("SyncNodesAndShips"+articleId+","+viewCount);
                    //更新到数据库
                    subjectService.updateById(subject);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        logger.info("结束保存点赞数 、浏览数");
    }

    /**
     * 商品浏览量
     */
    @Scheduled(cron = "0 0/10 * * * ? ")//每1分钟
    public void SyncGoodsView() {
        logger.info("开始保存点赞数 、浏览数SyncGoodsView");
        try {
            //先获取这段时间的浏览数
            Map<Object,Object> viewCountItem=redisUtil.hGetAll(Rediskey.GOODS_VIEWCOUNT_KEY);
            //然后删除redis里这段时间的浏览数
            redisUtil.delete(Rediskey.GOODS_VIEWCOUNT_KEY);
            if(!viewCountItem.isEmpty()){
                for(Object item :viewCountItem.keySet()){
                    String articleKey=item.toString();//viewcount_1
                    String[]  kv=articleKey.split("_");
                    Long articleId=Long.parseLong(kv[1]);
                    Integer viewCount=Integer.parseInt(viewCountItem.get(articleKey).toString());
                    PmsProduct subject = productMapper.selectById(articleId);
                    subject.setId(articleId);
                    subject.setHit(subject.getHit()+viewCount);
                    logger.info("SyncGoodsView"+articleId+","+viewCount);
                    //更新到数据库
                    productMapper.updateById(subject);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        logger.info("结束保存点赞数 、浏览数");
    }
}
