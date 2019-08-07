package com.zscat.mallplus.pms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zscat.mallplus.cms.entity.CmsSubject;
import com.zscat.mallplus.cms.service.ICmsSubjectService;
import com.zscat.mallplus.pms.entity.PmsFavorite;
import com.zscat.mallplus.pms.entity.PmsProduct;
import com.zscat.mallplus.pms.mapper.PmsFavoriteMapper;
import com.zscat.mallplus.pms.mapper.PmsProductMapper;
import com.zscat.mallplus.pms.service.IPmsFavoriteService;
import com.zscat.mallplus.sys.entity.SysStore;
import com.zscat.mallplus.sys.mapper.SysStoreMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-06-15
 */
@Service
public class PmsFavoriteServiceImpl extends ServiceImpl<PmsFavoriteMapper, PmsFavorite> implements IPmsFavoriteService {

    @Resource
    private PmsFavoriteMapper productCollectionRepository;
    @Resource
    private PmsProductMapper productMapper;
    @Resource
    private ICmsSubjectService subjectService;
    @Resource
    private SysStoreMapper storeMapper;
    @Override
    public int addProduct(PmsFavorite productCollection) {
        int count = 0;
        PmsFavorite query = new PmsFavorite();
        query.setObjId(productCollection.getObjId());
        query.setMemberId(productCollection.getMemberId());
        query.setType(productCollection.getType());
        PmsFavorite findCollection = productCollectionRepository.selectOne(new QueryWrapper<>(query));
        if (findCollection == null) {
            productCollection.setAddTime(new Date());
            productCollectionRepository.insert(productCollection);
            if (productCollection.getType()==1){
                PmsProduct subject = productMapper.selectById(productCollection.getObjId());
                subject.setId(productCollection.getObjId());
                subject.setGiftGrowth(subject.getGiftGrowth()+1);
                //更新到数据库
                productMapper.updateById(subject);
            }
            if (productCollection.getType()==2){
                CmsSubject subject = subjectService.getById(productCollection.getObjId());
                subject.setId(productCollection.getObjId());
                subject.setForwardCount(subject.getForwardCount()+1);
                //更新到数据库
                subjectService.updateById(subject);
            }

            count = 1;
        }else {
            if (productCollection.getType()==1){
                PmsProduct subject = productMapper.selectById(productCollection.getObjId());
                subject.setId(productCollection.getObjId());
                subject.setGiftGrowth(subject.getGiftGrowth()-1);
                //更新到数据库
                productMapper.updateById(subject);
            }
            if (productCollection.getType()==2){
                CmsSubject subject = subjectService.getById(productCollection.getObjId());
                subject.setId(productCollection.getObjId());
                subject.setForwardCount(subject.getForwardCount()-1);
                //更新到数据库
                subjectService.updateById(subject);
            }
            return productCollectionRepository.delete(new QueryWrapper<>(query));
        }
        return count;
    }



    @Override
    public List<PmsFavorite> listProduct(Long memberId, int type) {
        return productCollectionRepository.selectList(new QueryWrapper<PmsFavorite>().eq("member_id",memberId).eq("type",type));
    }
    @Override
    public List<PmsFavorite> listCollect(Long memberId) {
        return productCollectionRepository.selectList(new QueryWrapper<PmsFavorite>().eq("member_id",memberId));
    }
}
