package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.mapper.MessageOfflineMapper;
import com.li2n.im_server.pojo.MessageOffline;
import com.li2n.im_server.pojo.MessageTotal;
import com.li2n.im_server.pojo.UserInfo;
import com.li2n.im_server.service.IMessageOfflineService;
import com.li2n.im_server.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 私聊离线消息 服务实现类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Service
public class MessageOfflineServiceImpl extends ServiceImpl<MessageOfflineMapper, MessageOffline> implements IMessageOfflineService {

    @Value("${im-redis-key.login.client}")
    private String clientLoginKey;

    @Autowired
    private MessageOfflineMapper messageOfflineMapper;
    @Autowired
    private RedisCache redisCache;

    /**
     * 添加离线消息
     *
     * @param msg 消息对象
     */
    @Override
    public void insertMsg(MessageTotal msg) {
        UserInfo user = redisCache.getCacheObject(clientLoginKey + msg.getSendUsername());
        MessageOffline messageOffline = new MessageOffline();
        messageOffline.setSendNickname(user.getNickname());
        messageOffline.setSendUsername(msg.getSendUsername());
        messageOffline.setReceiveUsername(msg.getReceiveUsername());
        messageOffline.setMessageContentType(msg.getMessageContentType());
        messageOffline.setContent(msg.getContent());
        messageOffline.setFileUrl(msg.getFileUrl());
        messageOffline.setSendTime(msg.getSendTime());
        messageOfflineMapper.insert(messageOffline);
    }

    /**
     * 根据提供的参数查询多条离线消息记录
     *
     * @param username
     * @return
     */
    @Override
    public List<MessageOffline> selectByColumn(String username) {
        return messageOfflineMapper.selectOfflineMsgList(username);
    }

    /**
     * 根据提供的参数清除离线聊天记录
     *
     * @param username
     */
    @Override
    public void clearOfflineMsg(String username) {
        messageOfflineMapper.deleteMsg(username);
    }

}
