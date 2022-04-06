package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.entity.GroupMessage;
import com.li2n.im_server.mapper.MessageGroupMapper;
import com.li2n.im_server.service.IGroupListService;
import com.li2n.im_server.service.IGroupMessageService;
import com.li2n.im_server.service.IGroupService;
import com.li2n.im_server.utils.RedisCache;
import com.li2n.im_server.utils.TimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class GroupMessageServiceImpl extends ServiceImpl<MessageGroupMapper, GroupMessage> implements IGroupMessageService {


    @Autowired
    private MessageGroupMapper messageGroupMapper;
    @Autowired
    private IGroupListService groupListService;
    @Autowired
    private IGroupService groupService;
    @Autowired
    private RedisCache redisCache;

    @Value("${im-redis-key.user}")
    private String userKey;

    /**
     * 添加群消息记录
     *
     * @param msg
     */
    @Override
    public void insertMsg(GroupMessage msg) {
        messageGroupMapper.insert(msg);
    }

    /**
     * 查询用户的所有群消息记录
     *
     * @param username
     * @return
     */
    @Override
    public Map<String, List<GroupMessage>> msgListByUsername(String username) {
        HashMap<String, List<GroupMessage>> messageGroupHashMap = new HashMap<>();
        List<String> gidList = groupListService.gidListByUsername(username);
        if (gidList == null) {
            return null;
        }
        for (String gid : gidList) {
            List<GroupMessage> groupMsg = messageGroupMapper.msgListByGid(gid);
            for (GroupMessage groupMessage : groupMsg) {
                groupMessage.setUser(redisCache.getCacheObject(userKey + groupMessage.getSender()));
                groupMessage.setGroupInfo(groupService.groupInfoByGid(groupMessage.getGid()));
                groupMessage.setTime(TimeFormat.localDateTimeToString(groupMessage.getSendTime()));
                if (groupMessage.getSender().equals(username)) {
                    groupMessage.setSelf(1);
                } else {
                    groupMessage.setSelf(0);
                }
            }
            messageGroupHashMap.put(gid, groupMsg);
        }
        return messageGroupHashMap;
    }

    /**
     * 清空群聊天记录
     *
     * @param gid
     */
    @Override
    public void delMsgByGid(String gid) {
        messageGroupMapper.delete(new QueryWrapper<GroupMessage>().eq("gid", gid));
    }
}
