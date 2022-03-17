package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.mapper.NoticeServerMapper;
import com.li2n.im_server.pojo.NoticeServer;
import com.li2n.im_server.pojo.model.NoticeModel;
import com.li2n.im_server.service.INoticeServerService;
import com.li2n.im_server.utils.RedisCache;
import com.li2n.im_server.utils.TimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<NoticeModel> selectByUsername(String username) {
        username = "," + username + ",";
        List<NoticeModel> noticeModels = new ArrayList<>();
        List<NoticeServer> noticeServerList = noticeServerMapper.selectListByReceiveUsername(username);
        if (noticeServerList.isEmpty()) {
            return new ArrayList<>();
        }
        for (NoticeServer noticeServer : noticeServerList) {
            NoticeModel model = new NoticeModel();
            model.setTitle(noticeServer.getTitle());
            model.setFileName(noticeServer.getFileName());
            model.setFileUrl(noticeServer.getFileUrl());
            model.setContent(noticeServer.getContent());
            model.setPushNum(noticeServer.getPushNum());
            model.setPushTime(TimeFormat.localDateTimeToString(noticeServer.getSendTime()));
            noticeModels.add(model);
        }
        String key = "notice-server:" + username;
        List<NoticeModel> cacheNoticeList = redisCache.getCacheList(key);
        if (!cacheNoticeList.isEmpty()) {
            redisCache.deleteObject(key);
        }
        redisCache.setCacheList(key, noticeModels);
        return noticeModels;
    }
}
