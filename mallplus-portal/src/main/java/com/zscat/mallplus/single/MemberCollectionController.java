package com.zscat.mallplus.single;


import com.zscat.mallplus.cms.entity.CmsFavorite;
import com.zscat.mallplus.cms.service.ICmsFavoriteService;
import com.zscat.mallplus.pms.entity.PmsFavorite;
import com.zscat.mallplus.pms.service.IPmsFavoriteService;
import com.zscat.mallplus.utils.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员收藏管理Controller
 * Created by mallplus on 2018/8/2.
 */
@RestController
@Api(tags = "MemberCollectionController", description = "会员收藏和点赞管理管理")
@RequestMapping("/api/collection")
public class MemberCollectionController {
    @Autowired
    private IPmsFavoriteService memberCollectionService;
    @Autowired
    private ICmsFavoriteService cmsFavoriteService;

    @ApiOperation("添加和取消收藏 type 1 商品 2 文章")
    @PostMapping("favoriteSave")
    public Object favoriteSave(PmsFavorite productCollection) {
        int count = memberCollectionService.addProduct(productCollection);
        if (count > 0) {
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }

    @ApiOperation("删除收藏中的某个商品")
    @PostMapping(value = "/delete")
    public Object delete(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return new CommonResult().failed("参数为空");
        }
        List<Long> resultList = new ArrayList<>(ids.split(",").length);
        for (String s : ids.split(",")) {
            resultList.add(Long.valueOf(s));
        }
        if (memberCollectionService.removeByIds(resultList)) {
            return new CommonResult().success();
        }
        return new CommonResult().failed();
    }

    @ApiOperation("显示收藏列表")
    @GetMapping(value = "/listCollectByType")
    public Object listCollectByType( PmsFavorite productCollection) {
        List<PmsFavorite> memberProductCollectionList = memberCollectionService.listProduct(productCollection.getMemberId(),productCollection.getType());
        return new CommonResult().success(memberProductCollectionList);
    }
    @ApiOperation("显示收藏列表")
    @GetMapping(value = "/listCollect")
    public Object listCollect( PmsFavorite productCollection) {
        List<PmsFavorite> memberProductCollectionList = memberCollectionService.listCollect(productCollection.getMemberId());
        return new CommonResult().success(memberProductCollectionList);
    }


    @ApiOperation("添加和取消点赞 type 1 商品 2 文章")
    @PostMapping("likeSave")
    public Object likeSave(CmsFavorite productCollection) {
        int count = cmsFavoriteService.addProduct(productCollection);
        if (count > 0) {
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }

    @ApiOperation("删除点赞中的某个商品")
    @PostMapping(value = "/deleteLike")
    public Object deleteLike(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return new CommonResult().failed("参数为空");
        }
        List<Long> resultList = new ArrayList<>(ids.split(",").length);
        for (String s : ids.split(",")) {
            resultList.add(Long.valueOf(s));
        }

        if (cmsFavoriteService.removeByIds(resultList)) {
            return new CommonResult().success();
        }
        return new CommonResult().failed();
    }

    @ApiOperation("显示点赞列表")
    @GetMapping(value = "/listLikeByType")
    public Object listLikeByType( CmsFavorite productCollection) {
        List<CmsFavorite> memberProductCollectionList = cmsFavoriteService.listProduct(productCollection.getMemberId(),productCollection.getType());
        return new CommonResult().success(memberProductCollectionList);
    }
    @ApiOperation("显示点赞列表")
    @GetMapping(value = "/listLike")
    public Object listLike( CmsFavorite productCollection) {
        List<CmsFavorite> memberProductCollectionList = cmsFavoriteService.listCollect(productCollection.getMemberId());
        return new CommonResult().success(memberProductCollectionList);
    }
}
