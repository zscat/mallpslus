package com.zscat.mallplus.config;


import com.google.common.collect.Lists;
import com.zscat.mallplus.vo.ApiContext;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

//Spring boot方式
@EnableTransactionManagement
@Configuration
@MapperScan("com.zscat.mallplus.*.mapper*")
public class MybatisPlusConfig {
    private static final List<String> IGNORE_TENANT_TABLES = Lists.newArrayList("sys_admin_log", "sys_web_log", "sys_permission_category", "columns", "tables", "information_schema.columns", "information_schema.tables", "sys_user", "sys_store", "sys_permission");

    @Autowired
    private ApiContext apiContext;


}