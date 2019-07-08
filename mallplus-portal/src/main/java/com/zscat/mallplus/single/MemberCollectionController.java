package com.zscat.mallplus.single;


import com.zscat.mallplus.pms.entity.PmsFavorite;
import com.zscat.mallplus.pms.service.IPmsFavoriteService;
import com.zscat.mallplus.utils.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员收藏管理Controller
 * Created by mallplus on 2018/8/2.
 */
@Controller
@Api(tags = "MemberCollectionController", description = "会员收藏管理")
@RequestMapping("/api/collection")
public class MemberCollectionController {
    @Autowired
    private IPmsFavoriteService memberCollectionService;

    @ApiOperation("添加商品收藏")
    @ResponseBody
    @RequestMapping("favoriteSave")
    public Object favoriteSave(PmsFavorite productCollection) {
        int count = memberCollectionService.addProduct(productCollection);
        if (count > 0) {
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }


    @ApiOperation("删除收藏中的某个商品")
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return new CommonResult().failed("参数为空");
        }
        List<Long> resultList = new ArrayList<>(ids.split(",").length);
        for (String s : ids.split(",")) {
            resultList.add(Long.valueOf(s));
        }
         memberCollectionService.removeByIds(resultList);
        if (memberCollectionService.removeByIds(resultList)) {
            return new CommonResult().success();
        }
        return new CommonResult().failed();
    }

    @ApiOperation("显示关注列表")
    @RequestMapping(value = "/listCollectByType", method = RequestMethod.GET)
    @ResponseBody
    public Object listCollectByType( PmsFavorite productCollection) {
        List<PmsFavorite> memberProductCollectionList = memberCollectionService.listProduct(productCollection.getMemberId(),productCollection.getType());
        return new CommonResult().success(memberProductCollectionList);
    }
    @ApiOperation("显示关注列表")
    @RequestMapping(value = "/listCollect", method = RequestMethod.GET)
    @ResponseBody
    public Object listCollect( PmsFavorite productCollection) {
        List<PmsFavorite> memberProductCollectionList = memberCollectionService.listCollect(productCollection.getMemberId());
        return new CommonResult().success(memberProductCollectionList);
    }

}
