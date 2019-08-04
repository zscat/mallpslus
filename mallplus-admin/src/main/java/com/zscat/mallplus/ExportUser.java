package com.zscat.mallplus;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportUser {
    @Excel(name = "用户名", width = 10)
    private String username;

    @Excel(name = "创建时间", format = "yyyy-MM-dd", width = 15)
    private Date createTime;
}

