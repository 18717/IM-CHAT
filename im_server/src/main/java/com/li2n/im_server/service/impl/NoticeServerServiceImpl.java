package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.entity.NoticeServer;
import com.li2n.im_server.entity.User;
import com.li2n.im_server.mapper.NoticeServerMapper;
import com.li2n.im_server.service.INoticeServerService;
import com.li2n.im_server.utils.RedisCache;
import com.li2n.im_server.utils.TimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 推送通知 服务实现类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Service
public class NoticeServerServiceImpl extends ServiceImpl<NoticeServerMapper, NoticeServer> implements INoticeServerService {

    @Value("${im-redis-key.notice.server}")
    private String serverNoticeKey;
    @Value("${im-redis-key.user}")
    private String userKey;

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private NoticeServerMapper noticeServerMapper;

    /**
     * 添加通知
     *
     * @param noticeServer
     */
    @Override
    public void insertNotice(NoticeServer noticeServer) {
        noticeServerMapper.insert(noticeServer);
    }

    /**
     * 获取用户的所有通知
     *
     * @param username
     * @return
     */
    @Override
    public List<NoticeServer> selectByUsername(String username) {
        username = "," + username + ",";
        List<NoticeServer> noticeServerList = noticeServerMapper.selectListByReceiveUsername(username);
        if (noticeServerList.isEmpty()) {
            return new ArrayList<>();
        }
        for (NoticeServer noticeServer : noticeServerList) {
            User sender = redisCache.getCacheObject(userKey + noticeServer.getSender());
            noticeServer.setUser(sender);
            noticeServer.setTime(TimeFormat.localDateTimeToString(noticeServer.getSendTime()));
        }
        String key = serverNoticeKey + username;
        List<NoticeServer> cacheNoticeList = redisCache.getCacheList(key);
        if (!cacheNoticeList.isEmpty()) {
            redisCache.deleteObject(key);
        }
        redisCache.setCacheList(key, noticeServerList);
        return noticeServerList;
    }
}
