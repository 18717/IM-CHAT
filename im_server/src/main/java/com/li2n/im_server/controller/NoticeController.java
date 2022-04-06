package com.li2n.im_server.controller;

import com.li2n.im_server.entity.NoticeFriend;
import com.li2n.im_server.entity.NoticeGroup;
import com.li2n.im_server.entity.NoticeServer;
import com.li2n.im_server.service.INoticeFriendService;
import com.li2n.im_server.service.INoticeGroupService;
import com.li2n.im_server.service.INoticeServerService;
import com.li2n.im_server.utils.RedisCache;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-29 下午 1:16
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Value("${im-redis-key.notice.friend}")
    private String friendNoticeKey;
    @Value("${im-redis-key.notice.group}")
    private String groupNoticeKey;
    @Value("${im-redis-key.notice.server}")
    private String serverNoticeKey;

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private INoticeFriendService noticeFriendService;
    @Autowired
    private INoticeGroupService noticeGroupService;
    @Autowired
    private INoticeServerService noticeServerService;


    @ApiOperation(value = "获取好友通知")
    @GetMapping("/friend")
    public List<NoticeFriend> getNoticeFriendList(String username) {
        String key = friendNoticeKey + "," + username + ",";
        List<NoticeFriend> cacheList = redisCache.getCacheList(key);
        if (cacheList.isEmpty()) {
            cacheList = noticeFriendService.selectByUsername(username);
        }
        return cacheList;
    }
    @ApiOperation(value = "获取群通知")
    @GetMapping("/group")
    public List<NoticeGroup> getNoticeGroupList(String username) {
        String key = groupNoticeKey + "," + username + ",";
        List<NoticeGroup> cacheList = redisCache.getCacheList(key);
        if (cacheList.isEmpty()) {
            cacheList = noticeGroupService.selectByUsername(username);
        }
        return cacheList;
    }
    @ApiOperation(value = "获取服务器通知")
    @GetMapping("/server")
    public List<NoticeServer> getLoginUserHistoryNotice(String username) {
        String key = serverNoticeKey + "," + username + ",";
        List<NoticeServer> cacheList = redisCache.getCacheList(key);
        if (cacheList.isEmpty()) {
            cacheList = noticeServerService.selectByUsername(username);
        }
        return cacheList;
    }
}
