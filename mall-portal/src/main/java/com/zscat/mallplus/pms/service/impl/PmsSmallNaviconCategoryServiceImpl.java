package com.zscat.mallplus.pms.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zscat.mallplus.pms.entity.PmsSmallNaviconCategory;
import com.zscat.mallplus.pms.mapper.PmsSmallNaviconCategoryMapper;
import com.zscat.mallplus.pms.service.IPmsSmallNaviconCategoryService;
import com.zscat.mallplus.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class PmsSmallNaviconCategoryServiceImpl extends ServiceImpl<PmsSmallNaviconCategoryMapper, PmsSmallNaviconCategory> implements IPmsSmallNaviconCategoryService {
}
