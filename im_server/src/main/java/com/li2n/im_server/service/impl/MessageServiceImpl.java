package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.entity.Message;
import com.li2n.im_server.entity.User;
import com.li2n.im_server.mapper.MessageMapper;
import com.li2n.im_server.service.IMessageService;
import com.li2n.im_server.utils.RedisCache;
import com.li2n.im_server.utils.TimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 私聊消息汇总 服务实现类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

    @Autowired
    private MessageMapper msgMapper;
    @Autowired
    private RedisCache redisCache;
    @Value("${im-redis-key.user}")
    private String userKey;


    /**
     * 添加消息记录
     *
     * @param msg
     */
    @Override
    public void insertMsg(Message msg) {
        msgMapper.insert(msg);
    }

    /**
     * 根据提供的参数查询多条消息记录
     *
     * @param column
     * @param value
     * @return
     */
    @Override
    public List<Message> selectByColumn(String column, String value) {
        return msgMapper.selectList(new QueryWrapper<Message>().eq(column, value));
    }

    /**
     * 查询聊天记录
     * @param send 发送者用户名
     * @param receiver 接收者用户名
     * @return
     */
    @Override
    public List<Message> selectByMsgKey(String send, String receiver) {
        String sendTo = send + '@' + receiver;
        String receiverTo = receiver + '@' + send;
        List<Message> msgList = msgMapper.selectByMsgKey(sendTo, receiverTo);
        // 将self字段进行处理，方便前端消息展示
        for (Message msg : msgList) {
            if (msg.getSender().equals(send)) {
                msg.setSelf(1);
            } else {
                msg.setSelf(0);
            }
        }
        return msgList;
    }

    /**
     * 获得所有的聊天记录
     *
     * @return
     */
    @Override
    public List<Message> selectAll() {
        return msgMapper.selectAll();
    }

    /**
     * 查询有关登录用户所有的消息记录
     *
     * @param username
     * @return
     */
    @Override
    public List<Message> selectByUsername(String username) {
        List<Message> msgList = msgMapper.selectByUsername(username);
        for (Message msg : msgList) {
            msg.setTime(TimeFormat.localDateTimeToString(msg.getSendTime()));
            if (msg.getSender().equals(username)) {
                msg.setSelf(1);
            } else {
                msg.setSelf(0);
            }
        }
        return msgList;
    }

    /**
     * 删除有关的聊天记录
     *
     * @param sender
     * @param receiver
     */
    @Override
    public void deleteMsgByKey(String sender, String receiver) {
        String senderTo = sender + '@' + receiver;
        String receiverTo = receiver + '@' + sender;
        msgMapper.delete(new QueryWrapper<Message>().eq("mkey", senderTo));
        msgMapper.delete(new QueryWrapper<Message>().eq("mkey", receiverTo));
    }
}
