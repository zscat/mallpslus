package com.zscat.mallplus.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zscat.mallplus.ums.entity.UmsMember;
import com.zscat.mallplus.utils.CommonResult;
import com.zscat.mallplus.vo.AppletLoginParam;
import com.zscat.mallplus.vo.SmsCode;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author zscat
 * @since 2019-04-19
 */
public interface IUmsMemberService extends IService<UmsMember> {

    Object loginByWeixin(AppletLoginParam req);


    /**
     * 根据用户名获取会员
     */
    UmsMember getByUsername(String username);

    /**
     * 根据会员编号获取会员
     */
    UmsMember getById(Long id);

    /**
     * 用户注册
     */
    @Transactional
    CommonResult register(String phone, String password, String confim, String authCode);

    /**
     * 生成验证码
     */
    CommonResult generateAuthCode(String telephone);


    /**
     * 修改密码
     */
    @Transactional
    CommonResult updatePassword(String telephone, String password, String authCode);



    /**
     * 根据会员id修改会员积分
     */
    void updateIntegration(Long id, Integer integration);


    UmsMember queryByOpenId(String openId);


    Map<String, Object> login(String username, String password);

    String refreshToken(String token);

    Object register(UmsMember umsMember);

    SmsCode generateCode(String phone);

    Map<String, Object> loginByCode(String phone, String authCode);

    Object simpleReg(String phone, String password, String confimpassword);
}

