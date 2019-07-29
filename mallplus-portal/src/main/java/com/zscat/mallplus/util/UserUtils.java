package com.zscat.mallplus.util;


import com.zscat.mallplus.ums.entity.UmsMember;
import com.zscat.mallplus.vo.MemberDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @Auther: shenzhuan
 * @Date: 2019/4/5 19:48
 * @Description:
 */
public class UserUtils {
    public static UmsMember getCurrentMember() {
        try {
            SecurityContext ctx = SecurityContextHolder.getContext();
            Authentication auth = ctx.getAuthentication();
            if ("anonymousUser".equals(auth.getPrincipal())){
                return new UmsMember();
            }
            System.out.println( auth.getPrincipal());
            MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
            System.out.println(memberDetails);
            System.out.println(memberDetails.getUmsMember());
            System.out.println(memberDetails.getUsername());
            return memberDetails.getUmsMember();
        } catch (Exception e) {
            e.printStackTrace();
            return new UmsMember();
        }
    }
}
