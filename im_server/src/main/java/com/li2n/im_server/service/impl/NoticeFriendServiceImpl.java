package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.entity.NoticeFriend;
import com.li2n.im_server.entity.User;
import com.li2n.im_server.mapper.NoticeFriendMapper;
import com.li2n.im_server.service.INoticeFriendService;
import com.li2n.im_server.service.IUserService;
import com.li2n.im_server.utils.RedisCache;
import com.li2n.im_server.utils.TimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 好友通知 服务实现类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Service
public class NoticeFriendServiceImpl extends ServiceImpl<NoticeFriendMapper, NoticeFriend> implements INoticeFriendService {

    @Value("${im-redis-key.notice.friend}")
    private String friendNoticeKey;
    @Value("${im-redis-key.user}")
    private String userKey;

    @Autowired
    private NoticeFriendMapper noticeFriendMapper;
    @Autowired
    private IUserService userService;
    @Autowired
    private RedisCache redisCache;

    /**
     * 添加好友通知记录
     *
     * @param noticeFriend
     * @author 一杯香梨
     * @date 2022-3-13 下午 12:24
     */
    @Override
    public void addNoticeRecord(NoticeFriend noticeFriend) {
        noticeFriendMapper.insert(noticeFriend);
    }

    /**
     * 获取用户的所有好友通知
     *
     * @param username
     * @return
     */
    @Override
    public List<NoticeFriend> selectByUsername(String username) {
        List<NoticeFriend> noticeFriends = new ArrayList<>();
        List<NoticeFriend> noticeServerList = noticeFriendMapper.selectListByReceiveUsername(username);
        if (noticeServerList.isEmpty()) {
            return new ArrayList<>();
        }
        for (NoticeFriend noticeFriend : noticeServerList) {
            noticeFriend.setTime(TimeFormat.localDateTimeToString(noticeFriend.getCreateTime()));
            if (redisCache.getCacheObject(userKey) == null) {
                userService.updateRedisUser();
            }
            User user = redisCache.getCacheObject(userKey + noticeFriend.getSender());
            noticeFriend.setUser(user);
            noticeFriends.add(noticeFriend);
        }
        String key = friendNoticeKey + "," + username + ",";
        List<NoticeFriend> cacheNoticeList = redisCache.getCacheList(key);
        if (!cacheNoticeList.isEmpty()) {
            redisCache.deleteObject(key);
        }
        redisCache.setCacheList(key, noticeFriends);
        return noticeFriends;
    }

    /**
     * 更新好友通知
     *
     * @param noticeFriend
     */
    @Override
    public void updateNoticeFriend(NoticeFriend noticeFriend) {
        noticeFriendMapper.updateNotice(noticeFriend, TimeFormat.stringToLocalDateTime(noticeFriend.getFlagTime()));
        selectByUsername(noticeFriend.getSender());
    }
}
