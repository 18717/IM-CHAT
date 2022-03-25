package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.mapper.MessageTotalMapper;
import com.li2n.im_server.pojo.MessageTotal;
import com.li2n.im_server.pojo.model.MessageModel;
import com.li2n.im_server.service.IMessageTotalService;
import com.li2n.im_server.utils.RedisCache;
import com.li2n.im_server.utils.TimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
public class MessageTotalServiceImpl extends ServiceImpl<MessageTotalMapper, MessageTotal> implements IMessageTotalService {

    @Autowired
    private MessageTotalMapper msgMapper;
    @Autowired
    private RedisCache redisCache;

    /**
     * 添加消息记录
     *
     * @param msg
     */
    @Override
    public void insertMsg(MessageTotal msg) {
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
    public List<MessageTotal> selectByColumn(String column, String value) {
        return msgMapper.selectList(new QueryWrapper<MessageTotal>().eq(column, value));
    }

    /**
     * 查询聊天记录
     * @param send 发送者用户名
     * @param receiver 接收者用户名
     * @return
     */
    @Override
    public List<MessageTotal> selectByMsgKey(String send, String receiver) {
        String sendTo = send + '@' + receiver;
        String receiverTo = receiver + '@' + send;
        List<MessageTotal> msgList = msgMapper.selectByMsgKey(sendTo, receiverTo);
        // 将self字段进行处理，方便前端消息展示
        for (MessageTotal msg : msgList) {
            if (msg.getSendUsername().equals(send)) {
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
    public List<MessageTotal> selectAll() {
        return msgMapper.selectAll();
    }

    /**
     * 查询有关登录用户所有的消息记录
     *
     * @param username
     * @return
     */
    @Override
    public List<MessageTotal> selectByUsername(String username) {
        List<MessageTotal> msgList = msgMapper.selectByUsername(username);
        for (MessageTotal msg : msgList) {
            msg.setSendTimeStr(TimeFormat.localDateTimeToString(msg.getSendTime()));
            if (msg.getSendUsername().equals(username)) {
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
        msgMapper.delete(new QueryWrapper<MessageTotal>().eq("mkey", senderTo));
        msgMapper.delete(new QueryWrapper<MessageTotal>().eq("mkey", receiverTo));
    }
}
