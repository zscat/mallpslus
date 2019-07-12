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
 * @date 2019-07-12 14:58:48
 */
@Data
@TableName("sms_msgresult")
public class SmsMsgresult implements Serializable {
	private static final long serialVersionUID = 1L;
	

@ApiModelProperty(value = "")
@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

@ApiModelProperty(value = "结果值")
@TableField("result")
	private Integer result;

@ApiModelProperty(value = "返回信息")
@TableField("errmsg")
	private String errmsg;

@ApiModelProperty(value = "手机号")
@TableField("mobile")
	private String mobile;

@ApiModelProperty(value = "地域")
@TableField("nationcode")
	private String nationcode;

@ApiModelProperty(value = "短信任务id")
@TableField("sid")
	private String sid;

@ApiModelProperty(value = "")
@TableField("fee")
	private Integer fee;

@ApiModelProperty(value = "本地任务id")
@TableField("localsid")
	private String localsid;

}
