package com.zscat.mallplus.enums;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Administrator on 2019/8/9.
 */
public class ConstansValue {
    public static final List<String> IGNORE_TENANT_TABLES = Lists.newArrayList("sys_admin_log", "sys_web_log", "sys_permission_category", "columns", "tables", "information_schema.columns", "information_schema.tables", "sys_user", "sys_store", "sys_permission");

}
