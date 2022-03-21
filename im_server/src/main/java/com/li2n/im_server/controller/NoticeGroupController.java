package com.li2n.im_server.controller;


import com.li2n.im_server.pojo.NoticeFriend;
import com.li2n.im_server.pojo.NoticeGroup;
import com.li2n.im_server.service.INoticeGroupService;
import com.li2n.im_server.utils.RedisCache;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * @since 2022-03-21
 */
@RestController
@RequestMapping("/notice-group")
public class NoticeGroupController {

    @Value("${im-redis-key.notice.group}")
    private String groupNoticeKey;

    @Autowired
    private INoticeGroupService noticeGroupService;
    @Autowired
    private RedisCache redisCache;

    @ApiOperation(value = "获取用户的所有群通知")
    @GetMapping("/history/username")
    public List<NoticeGroup> getLoginUserHistoryNotice(String username) {
        String key = groupNoticeKey + "," + username + ",";
        List<NoticeGroup> cacheList = redisCache.getCacheList(key);
        if (cacheList.isEmpty()) {
            cacheList = noticeGroupService.selectByUsername(username);
        }
        return cacheList;
    }

}
