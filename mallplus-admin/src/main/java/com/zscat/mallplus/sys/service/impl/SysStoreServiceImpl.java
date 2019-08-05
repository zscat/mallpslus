package com.zscat.mallplus.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zscat.mallplus.sys.entity.SysStore;
import com.zscat.mallplus.sys.entity.SysUser;
import com.zscat.mallplus.sys.mapper.SysStoreMapper;
import com.zscat.mallplus.sys.mapper.SysUserMapper;
import com.zscat.mallplus.sys.service.ISysStoreService;
import com.zscat.mallplus.vo.ApiContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-05-18
 */
@Service
public class SysStoreServiceImpl extends ServiceImpl<SysStoreMapper, SysStore> implements ISysStoreService {

    @Resource
    private SysStoreMapper storeMapper;
    @Resource
    private SysUserMapper userMapper;

    @Resource
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ApiContext apiContext;
    @Transactional
    @Override
    public boolean saveStore(SysStore entity) {
        storeMapper.insert(entity);
        SysUser user = new SysUser();
        user.setUsername(entity.getName());
        user.setStatus(1);
        user.setSupplyId(1L);
        user.setPassword(passwordEncoder.encode(entity.getSupportName()));
        user.setCreateTime(new Date());
        user.setIcon(entity.getLogo());
        user.setNickName(entity.getName());
        //user.setStoreId(entity.getId());
        user.setEmail(entity.getSupportPhone());
        apiContext.setCurrentProviderId(entity.getId());
        return userMapper.insert(user) > 0;
    }
}
