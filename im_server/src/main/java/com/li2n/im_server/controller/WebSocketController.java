package com.li2n.im_server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.li2n.im_server.entity.*;
import com.li2n.im_server.exception.MyException;
import com.li2n.im_server.service.*;
import com.li2n.im_server.utils.RedisCache;
import com.li2n.im_server.utils.TimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-6 下午 12:24
 */

@Controller
public class WebSocketController {

    @Value("${im-redis-key.notice.server}")
    private String serverNoticeKey;
    @Value("${im-redis-key.notice.friend}")
    private String friendNoticeKey;
    @Value("${im-redis-key.notice.group}")
    private String groupNoticeKey;
    @Value("${im-redis-key.usernames}")
    private String usernames;
    @Value("${im-redis-key.user}")
    private String userKey;
    @Value("${im-redis-key.login.client}")
    private String clientLoginKey;
    @Value("${im-redis-key.login.server}")
    private String serverLoginKey;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private IMessageService msgService;
    @Autowired
    private IMessageService messageTotalService;
    @Autowired
    private INoticeFriendService noticeFriendService;
    @Autowired
    private INoticeServerService noticeServerService;
    @Autowired
    private IFriendListService userFriendListService;
    @Autowired
    private IGroupService groupService;
    @Autowired
    private INoticeGroupService noticeGroupService;
    @Autowired
    private IGroupMessageService messageGroupService;

    @MessageMapping("/ws/client/chat")
    public void handleMsg(Authentication authentication, Message message) {
        User user = (User) authentication.getPrincipal();
        user.setPassword(null);
        String msgKey = user.getUsername() + "@" + message.getReceiver();

        message.setMkey(msgKey);
        message.setUser(user);
        message.setSender(user.getUsername());
        message.setSendTime(TimeFormat.stringToLocalDateTime(message.getTime()));

        User receiveUser = redisCache.getCacheObject(clientLoginKey + message.getReceiver());
        if (!Objects.isNull(receiveUser)) {
            simpMessagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/chat", message);
        }
        message.setSelf(null);
        msgService.insertMsg(message);
    }

    @MessageMapping("/ws/group/chat")
    public void groupChat(GroupMessage message) {

        message.setSendTime(TimeFormat.stringToLocalDateTime(message.getTime()));
        message.setSelf(null);
        messageGroupService.insertMsg(message);

        User sender = redisCache.getCacheObject(clientLoginKey + message.getSender());
        GroupInfo groupInfo = groupService.groupInfoByGid(message.getGid());
        message.setUser(sender);
        message.setGroupInfo(groupInfo);

        List<String> groupMembers = groupService.getGroupMembers(message.getGid());
        groupMembers.add(message.getGroupInfo().getLeader());

        for (String username : groupMembers) {
            if (!username.equals(message.getSender())) {
                simpMessagingTemplate.convertAndSendToUser(username, "/queue/group/chat", message);
            }
        }
    }

    @MessageMapping("/ws/server/notice")
    public void serverToUser(NoticeServer noticeServer) {

        String receiver = noticeServer.getReceiver();
        List<String> usernameList = redisCache.getCacheList(usernames);
        noticeServer.setSendTime(TimeFormat.stringToLocalDateTime(noticeServer.getTime()));
        noticeServer.setPushNum(usernameList.size());
        User user = redisCache.getCacheObject(userKey + noticeServer.getSender());
        noticeServer.setUser(user);

        noticeServer.setSendTime(TimeFormat.stringToLocalDateTime(noticeServer.getTime()));
        noticeServer.setPushNum(usernameList.size());

        if (noticeServer.isCheckedNotice()) {
            receiver = receiver.substring(1, receiver.length() - 1);
            String[] usernameArr = receiver.split(",");
            noticeServer.setPushNum(usernameArr.length);
            for (String username : usernameArr) {
                updateRedisServerNotice(username, noticeServer);
                noticeServerService.insertNotice(noticeServer);
                simpMessagingTemplate.convertAndSendToUser(username, "/topic/notice/server", noticeServer);
            }
        } else if (noticeServer.isAllNotice()) {
            noticeServerService.insertNotice(noticeServer);
            simpMessagingTemplate.convertAndSend("/topic/server", noticeServer);
        }
    }

    /**
     * 转发添加/删除好友的消息
     *
     * @param noticeFriend
     */
    @MessageMapping("/ws/friend/send")
    public void sendFriendRequest(NoticeFriend noticeFriend){
        User sender = redisCache.getCacheObject(clientLoginKey + noticeFriend.getSender());
        noticeFriend.setUser(sender);
        noticeFriend.setCreateTime(TimeFormat.stringToLocalDateTime(noticeFriend.getTime()));
        noticeFriend.setVerified(1);
        noticeFriend.setFlag(1);
        // 添加/删除
        if (noticeFriend.isAdd()) {
            noticeFriend.setAdd(1);
            noticeFriend.setDel(0);
            if (noticeFriend.getResult() != null) {
                if (noticeFriend.getResult()) {
                    noticeFriend.setTitle(noticeFriend.getTitle());
                    // 更新好友列表
                    noticeFriend.setConfirm(1);
                    userFriendListService.addFriendRecord(noticeFriend.getSender(), noticeFriend.getReceiver());
                } else {
                    noticeFriend.setTitle(noticeFriend.getTitle());
                    noticeFriend.setContent(noticeFriend.getContent());
                }
                noticeFriend.setFlagTime(noticeFriend.getFlagTime());
                noticeFriend.setUpdateTime(LocalDateTime.now());
                noticeFriendService.updateNoticeFriend(noticeFriend);
            } else {
                noticeFriend.setConfirm(0);
                noticeFriend.setFlag(0);
                noticeFriend.setTitle(noticeFriend.getTitle());
                noticeFriend.setContent(noticeFriend.getContent());
                noticeFriend.setVerified(0);
            }
        } else if (noticeFriend.isDel()) {
            noticeFriend.setTitle(noticeFriend.getTitle());
            noticeFriend.setDel(1);
            noticeFriend.setAdd(0);
            noticeFriend.setConfirm(1);
            messageTotalService.deleteMsgByKey(noticeFriend.getSender(), noticeFriend.getReceiver());
        } else {
            try {
                throw new MyException("未知业务类型", noticeFriend);
            } catch (MyException e) {
                e.printStackTrace();
            }
        }
        noticeFriendService.addNoticeRecord(noticeFriend);
        updateRedisFriendNotice(noticeFriend.getReceiver(), noticeFriend);
        simpMessagingTemplate.convertAndSendToUser(noticeFriend.getReceiver(),
                "/topic/notice/friend", noticeFriend);

    }

    /**
     * 转发添加/退群的消息
     *
     * @param noticeGroup
     */
    @MessageMapping("/ws/group/send")
    public void sendGroupRequest(NoticeGroup noticeGroup) {
        GroupInfo groupInfo = groupService.groupInfoByGid(noticeGroup.getGid());
        noticeGroup.setGroupInfo(groupInfo);
        noticeGroup.setCreateTime(TimeFormat.stringToLocalDateTime(noticeGroup.getTime()));

        User user = redisCache.getCacheObject(clientLoginKey + noticeGroup.getSender());
        user.setPassword(null);
        noticeGroup.setUser(user);
        noticeGroup.setGroupInfo(groupService.getOne(
                new QueryWrapper<GroupInfo>().eq("gid", noticeGroup.getGid())));

        // 业务判断处理
        if (noticeGroup.isDismiss()) {
            StringBuilder sb = new StringBuilder(noticeGroup.getMembers());
            sb.delete(0, noticeGroup.getSender().length() + 1);
            String[] usernames = String.valueOf(sb).split(",");
            for (String username : usernames) {
                if (!"".equals(username)) {
                    noticeGroup.setReceiver(username);
                    noticeGroupService.addNoticeRecord(noticeGroup);
                    updateRedisGroupNotice(username, noticeGroup);
                    simpMessagingTemplate.convertAndSendToUser(noticeGroup.getReceiver(), "/topic/notice/group", noticeGroup);
                }
            }
        } else {
            if (noticeGroup.isJoin()) {
                if (noticeGroup.getResult() != null) {
                    if (noticeGroup.isConfirm()) {
                        groupService.joinGroup(noticeGroup);
                    }
                    noticeGroup.setFlag(1);
                    noticeGroup.setUpdateTime(TimeFormat.stringToLocalDateTime(noticeGroup.getTime()));
                    noticeGroupService.updateNoticeGroup(noticeGroup);
                } else {
                    noticeGroup.setFlag(0);
                    noticeGroup.setVerified(0);
                }
            } else if (noticeGroup.isQuit() || noticeGroup.isForceQuit()) {
                noticeGroup.setConfirm(1);
                noticeGroup.setVerified(1);
                noticeGroup.setFlag(1);
            } else {
                try {
                    throw new MyException("未知业务类型", noticeGroup);
                } catch (MyException e) {
                    e.printStackTrace();
                }
            }
            // 操作数据库和redis
            noticeGroupService.addNoticeRecord(noticeGroup);
            updateRedisGroupNotice(noticeGroup.getReceiver(), noticeGroup);
            simpMessagingTemplate.convertAndSendToUser(noticeGroup.getReceiver(),
                    "/topic/notice/group", noticeGroup);
        }

    }

    /**
     * 更新redis中的系统通知
     *
     * @param username
     * @param noticeServer
     */
    private void updateRedisServerNotice(String username, NoticeServer noticeServer) {
        String key = serverNoticeKey + "," + username + ",";
        List<NoticeServer> cacheList = redisCache.getCacheList(key);
        if (cacheList.isEmpty()) {
            cacheList = noticeServerService.selectByUsername(username);
        }
        cacheList.add(noticeServer);
        redisCache.deleteObject(key);
        redisCache.setCacheList(key, cacheList);
    }

    /**
     * 更新redis中的好友通知
     *
     * @param username
     * @param noticeFriend
     */
    private void updateRedisFriendNotice(String username, NoticeFriend noticeFriend) {
        String key = friendNoticeKey + "," + username + ",";
        List<NoticeFriend> cacheList = redisCache.getCacheList(key);
        if (cacheList.isEmpty()) {
            noticeFriendService.selectByUsername(username);
        } else {
            cacheList.add(noticeFriend);
            redisCache.deleteObject(key);
            redisCache.setCacheList(key, cacheList);
        }
    }

    /**
     * 更新redis中的群通知
     *
     * @param username
     * @param noticeGroup
     */
    private void updateRedisGroupNotice(String username, NoticeGroup noticeGroup) {
        String key = groupNoticeKey + "," + username + ",";
        List<NoticeGroup> cacheList = redisCache.getCacheList(key);
        if (cacheList.isEmpty()) {
            noticeGroupService.selectByUsername(username);
        } else {
            cacheList.add(noticeGroup);
            redisCache.deleteObject(key);
            redisCache.setCacheList(key, cacheList);
        }
    }

}
