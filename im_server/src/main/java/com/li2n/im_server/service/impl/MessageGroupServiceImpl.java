package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.mapper.MessageGroupMapper;
import com.li2n.im_server.pojo.MessageGroup;
import com.li2n.im_server.service.IGroupListService;
import com.li2n.im_server.service.IMessageGroupService;
import com.li2n.im_server.utils.TimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 群消息汇总 服务实现类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Service
public class MessageGroupServiceImpl extends ServiceImpl<MessageGroupMapper, MessageGroup> implements IMessageGroupService {


    @Autowired
    private MessageGroupMapper messageGroupMapper;
    @Autowired
    private IGroupListService groupListService;

    /**
     * 添加群消息记录
     *
     * @param msg
     */
    @Override
    public void insertMsg(MessageGroup msg) {
        messageGroupMapper.insert(msg);
    }

    /**
     * 查询用户的所有群消息记录
     *
     * @param username
     * @return
     */
    @Override
    public Map<String, List<MessageGroup>> msgListByUsername(String username) {
        HashMap<String, List<MessageGroup>> messageGroupHashMap = new HashMap<>();
        List<String> gidList = groupListService.gidListByUsername(username);
        for (String gid : gidList) {
            List<MessageGroup> groupMsg = messageGroupMapper.msgListByGid(gid);
            for (MessageGroup messageGroup : groupMsg) {
                messageGroup.setSendTimeStr(TimeFormat.localDateTimeToString(messageGroup.getSendTime()));
                if (messageGroup.getSenderUsername().equals(username)) {
                    messageGroup.setSelf(1);
                } else {
                    messageGroup.setSelf(0);
                }
            }
            messageGroupHashMap.put(gid, groupMsg);
        }
        return messageGroupHashMap;
    }
}
