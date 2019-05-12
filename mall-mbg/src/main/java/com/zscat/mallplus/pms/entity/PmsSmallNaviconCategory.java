package com.zscat.mallplus.pms.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;


/**
 * @Description:小程序首页nav管理
 *
 * @author zscat
 * @email 951449465@qq.com
 * @date 2019-05-08 00:09:37
 */
@Data
@TableName("pms_small_navicon_category")
public class PmsSmallNaviconCategory implements Serializable {
	private static final long serialVersionUID = 1L;


@ApiModelProperty(value = "小程序首页分类ID")
@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

@ApiModelProperty(value = "分类名称")
@TableField("title")
	private String title;

@ApiModelProperty(value = "分类图标")
@TableField("icon")
	private String icon;

@ApiModelProperty(value = "跳转页面")
@TableField("summary")
	private String summary;

@ApiModelProperty(value = "跳转类型")
@TableField("content")
	private String content;

@ApiModelProperty(value = "排序")
@TableField("sort")
	private Integer sort;

}
