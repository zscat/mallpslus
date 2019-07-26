package com.zscat.mallplus.sms.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;


/**
 * @Description:
 * 
 * @author cxh
 * @email 274567491@qq.com
 * @date 2019-07-12 14:58:54
 */
@Data
@TableName("sms_phonenum")
public class SmsPhonenum implements Serializable {
	private static final long serialVersionUID = 1L;
	

@ApiModelProperty(value = "")
@TableId(value = "id", type = IdType.AUTO)
	private Long id;

@ApiModelProperty(value = "手机号码")
@TableField("phoneNum")
	private String phonenum;

@ApiModelProperty(value = "所属用户")
@TableField("userId")
	private Long userid;

}
