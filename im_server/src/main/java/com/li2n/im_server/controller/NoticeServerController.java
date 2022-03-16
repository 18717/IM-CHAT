package com.li2n.im_server.controller;


import com.li2n.im_server.pojo.NoticeServer;
import com.li2n.im_server.pojo.model.NoticeModel;
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
 * 推送通知 前端控制器
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@RestController
@RequestMapping("/notice-server")
public class NoticeServerController {

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private INoticeServerService noticeServerService;

    @ApiOperation(value = "获取用户的所有通知")
    @GetMapping("/history/username")
    public List<NoticeModel> getLoginUserHistoryNotice(String username) {
        String key = "notice-server:" + "," + username + ",";
        List<NoticeModel> cacheList = redisCache.getCacheList(key);
        if (cacheList.isEmpty()) {
            cacheList = noticeServerService.selectByUsername(username);
        }
        return cacheList;
    }

}
