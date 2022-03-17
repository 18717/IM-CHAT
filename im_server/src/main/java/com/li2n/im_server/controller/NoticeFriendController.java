package com.li2n.im_server.controller;


import com.li2n.im_server.pojo.NoticeFriend;
import com.li2n.im_server.pojo.model.NoticeModel;
import com.li2n.im_server.service.INoticeFriendService;
import com.li2n.im_server.service.INoticeServerService;
import com.li2n.im_server.utils.RedisCache;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 好友通知 前端控制器
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@RestController
@RequestMapping("/notice-friend")
public class NoticeFriendController {

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private INoticeFriendService noticeFriendService;

    @ApiOperation(value = "获取用户的所有好友通知")
    @GetMapping("/history/username")
    public List<NoticeFriend> getLoginUserHistoryNotice(String username) {
        String key = "notice-friend:" + "," + username + ",";
        List<NoticeFriend> cacheList = redisCache.getCacheList(key);
        if (cacheList.isEmpty()) {
            cacheList = noticeFriendService.selectByUsername(username);
        }
        return cacheList;
    }

}
