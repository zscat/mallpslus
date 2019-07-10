package com.zscat.mallplus.sms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zscat.mallplus.sms.entity.SmsBasicGifts;
import com.zscat.mallplus.sms.mapper.SmsBasicGiftsMapper;
import com.zscat.mallplus.sms.service.ISmsBasicGiftsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-07-07
 */
@Service
public class SmsBasicGiftsServiceImpl extends ServiceImpl<SmsBasicGiftsMapper, SmsBasicGifts> implements ISmsBasicGiftsService {

    @Resource
    private  SmsBasicGiftsMapper giftsMapper;
    @Override
    public int updateStatus(Long id, Integer status) {
        SmsBasicGifts gifts = new SmsBasicGifts();
        gifts.setId(id);
        if (status==1){
            gifts.setStatus(0);
        }else{
            gifts.setStatus(1);
        }
        return giftsMapper.updateById(gifts);
    }

    @Override
    public List<SmsBasicGifts> matchGoodsMk(Long id) {
        return null;
    }
}
