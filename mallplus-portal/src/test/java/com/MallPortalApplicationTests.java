package com;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zscat.mallplus.ums.entity.UmsMember;
import com.zscat.mallplus.ums.service.IUmsMemberService;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest(classes = MallPortalApplicationTests.class)
@RunWith(SpringRunner.class)
@Log4j2
public class MallPortalApplicationTests {

    @Resource
    IUmsMemberService sysAdminLogMapper;

    @Test
    public void contextLoads() {
       List<UmsMember> log =  sysAdminLogMapper.list(new QueryWrapper<UmsMember>().between("create_time","2018-03-03","2018-09-03"));
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }
}
