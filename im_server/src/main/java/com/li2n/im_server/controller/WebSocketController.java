package com.li2n.im_server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.li2n.im_server.pojo.*;
import com.li2n.im_server.pojo.model.FriendModel;
import com.li2n.im_server.pojo.model.GroupModel;
import com.li2n.im_server.pojo.model.MessageModel;
import com.li2n.im_server.pojo.model.NoticeModel;
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
    @Value("${im-redis-key.login.client}")
    private String clientLoginKey;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private IMessageTotalService msgService;
    @Autowired
    private IMessageOfflineService iMessageOfflineService;
    @Autowired
    private IMessageTotalService messageTotalService;
    @Autowired
    private INoticeFriendService noticeFriendService;
    @Autowired
    private INoticeServerService noticeServerService;
    @Autowired
    private IUserFriendListService userFriendListService;
    @Autowired
    private IGroupInfoService groupInfoService;
    @Autowired
    private INoticeGroupService noticeGroupService;
    @Autowired
    private IMessageGroupService messageGroupService;

    @MessageMapping("/ws/client/chat")
    public void handleMsg(Authentication authentication, MessageModel model) {
        UserInfo user = (UserInfo) authentication.getPrincipal();
        String msgKey = user.getUsername() + "@" + model.getReceiveUsername();
        MessageTotal msg = new MessageTotal();

        msg.setMkey(msgKey);
        msg.setSendUsername(user.getUsername());
        msg.setReceiveUsername(model.getReceiveUsername());
        msg.setMessageContentType(model.getMessageContentType());
        msg.setContent(model.getContent());
        msg.setFileUrl(model.getFileUrl());
        msg.setSendTime(TimeFormat.stringToLocalDateTime(model.getSendTimeStr()));
        msg.setSelf(model.getSelf());

        model.setSendNickname(user.getNickname());
        model.setSendUsername(user.getUsername());

        UserInfo receiveUser = redisCache.getCacheObject(clientLoginKey + model.getReceiveUsername());
        if (!Objects.isNull(receiveUser)) {
            msg.setOnline(1);
            simpMessagingTemplate.convertAndSendToUser(model.getReceiveUsername(), "/queue/chat", model);
        } else {
            msg.setOnline(0);
            iMessageOfflineService.insertMsg(msg);
        }
        msgService.insertMsg(msg);
    }

    @MessageMapping("/ws/group/chat")
    public void groupChat(MessageModel model) {
        MessageGroup msg = new MessageGroup();
        msg.setGid(model.getGid());
        msg.setSenderUsername(model.getSendUsername());
        msg.setMessageContentType(model.getMessageContentType());
        msg.setContent(model.getContent());
        msg.setFileUrl(model.getFileUrl());
        msg.setSendTime(TimeFormat.stringToLocalDateTime(model.getSendTimeStr()));
        msg.setSelf(null);
        messageGroupService.insertMsg(msg);

        List<String> groupMembers = groupInfoService.getGroupMembers(model.getGid());
        groupMembers.add(model.getGroupMaster());

        for (String username : groupMembers) {
            if (!username.equals(model.getSendUsername())) {
                simpMessagingTemplate.convertAndSendToUser(username, "/queue/group/chat", model);
            }
        }
    }

    @MessageMapping("/ws/server/notice")
    public void serverToUser(NoticeModel model) {

        String receiveUsername = model.getReceiveUsername();
        List<String> usernameList = redisCache.getCacheList(usernames);
        NoticeServer noticeServer = new NoticeServer();
        noticeServer.setTitle(model.getTitle());
        noticeServer.setSendUsername("server");
        noticeServer.setReceiveUsername(model.getReceiveUsername());
        noticeServer.setNoticeType(model.getPush());
        noticeServer.setFileUrl(model.getFileUrl());
        noticeServer.setContent(model.getContent());
        noticeServer.setContentType("text");
        noticeServer.setSendTime(TimeFormat.stringToLocalDateTime(model.getPushTime()));
        noticeServer.setPushNum(usernameList.size());

        model.setPushTime(TimeFormat.localDateTimeToString(noticeServer.getSendTime()));
        model.setPushNum(usernameList.size());

        if (!"all".equals(model.getPush())) {
            receiveUsername = receiveUsername.substring(1, receiveUsername.length() - 1);
            String[] usernameArr = receiveUsername.split(",");
            noticeServer.setPushNum(usernameArr.length);
            model.setPushNum(usernameArr.length);
            for (String username : usernameArr) {
                updateRedisServerNotice(username, model);
                simpMessagingTemplate.convertAndSendToUser(username, "/topic/notice/server", model);
            }
        } else {
            for (String username : usernameList) {
                updateRedisServerNotice(username, model);
            }
            simpMessagingTemplate.convertAndSend("/topic/chat", model);
        }
        noticeServerService.insertNotice(noticeServer);
    }

    @MessageMapping("/ws/server/chat")
    public void radioMsg(MessageModel model) {
        model.setSendUsername("server");
        simpMessagingTemplate.convertAndSend("/topic/chat", model);
    }

    /**
     * 转发添加/删除好友的消息
     *
     * @param model
     */
    @MessageMapping("/ws/friend/send")
    public void sendFriendRequest(FriendModel model) {

        UserInfo receiveUser = redisCache.getCacheObject(clientLoginKey + model.getReceiveUsername());
        NoticeFriend noticeFriend = new NoticeFriend();
        noticeFriend.setAvatarUrl(model.getAvatarUrl());
        noticeFriend.setSendNickname(model.getSendNickname());
        noticeFriend.setSendUsername(model.getSendUsername());
        noticeFriend.setReceiveUsername(model.getReceiveUsername());
        noticeFriend.setCreateTime(TimeFormat.stringToLocalDateTime(model.getSendTime()));
        noticeFriend.setSendTime(model.getSendTime());
        noticeFriend.setConfirm(1);
        noticeFriend.setVerified(1);
        noticeFriend.setFlag(1);
        noticeFriend.setAdd(0);
        noticeFriend.setDel(0);
        noticeFriend.setOnline(null);

        // 添加/删除
        if (model.isAdd()) {
            noticeFriend.setAdd(1);
            if (model.getResult() != null) {
                if (model.getResult()) {
                    noticeFriend.setTitle(model.getTitle());
                    // 更新好友列表
                    FriendModel friendModel = new FriendModel();
                    friendModel.setSendUsername(model.getReceiveUsername());
                    friendModel.setReceiveUsername(model.getSendUsername());
                    userFriendListService.addFriendRecord(friendModel);
                } else {
                    noticeFriend.setConfirm(0);
                    noticeFriend.setTitle(model.getTitle());
                    noticeFriend.setContent(model.getContent());
                }
                noticeFriend.setFlagTime(model.getFlagTime());
                noticeFriend.setUpdateTime(LocalDateTime.now());
                noticeFriendService.updateNoticeFriend(noticeFriend);
            } else {
                noticeFriend.setFlag(0);
                noticeFriend.setTitle(model.getTitle());
                noticeFriend.setContent(model.getContent());
                noticeFriend.setVerified(0);
            }
        } else if (model.isDel()) {
            noticeFriend.setTitle(model.getTitle());
            noticeFriend.setDel(1);
            messageTotalService.deleteMsgByKey(noticeFriend.getSendUsername(), noticeFriend.getReceiveUsername());
        }
        noticeFriendService.addNoticeRecord(noticeFriend);
        updateRedisFriendNotice(model.getReceiveUsername(), noticeFriend);
        simpMessagingTemplate.convertAndSendToUser(model.getReceiveUsername(), "/topic/notice/friend", model);

    }

    /**
     * 转发添加/退群的消息
     *
     * @param model
     */
    @MessageMapping("/ws/group/send")
    public void sendGroupRequest(GroupModel model) {
        NoticeGroup noticeGroup = new NoticeGroup();
        noticeGroup.setSenderUsername(model.getSenderUsername());
        noticeGroup.setReceiverUsername(model.getReceiverUsername());
        noticeGroup.setGid(model.getGid());
        noticeGroup.setGroupName(model.getGroupName());
        noticeGroup.setTitle(model.getTitle());
        noticeGroup.setContent(model.getContent());
        noticeGroup.setJoin(0);
        noticeGroup.setQuit(0);
        noticeGroup.setForceQuit(0);
        noticeGroup.setOnline(null);
        noticeGroup.setFlag(1);
        noticeGroup.setVerified(1);
        noticeGroup.setConfirm(1);
        noticeGroup.setCreateTime(TimeFormat.stringToLocalDateTime(model.getSendTime()));
        noticeGroup.setSendTime(model.getSendTime());

        UserInfo user = redisCache.getCacheObject(clientLoginKey + model.getSenderUsername());
        UserInfo userInfo = new UserInfo();
        userInfo.setAvatar(user.getAvatar());
        userInfo.setNickname(user.getNickname());
        userInfo.setGender(user.getGender());
        noticeGroup.setUserInfo(userInfo);

        noticeGroup.setGroupInfo(groupInfoService.getOne(new QueryWrapper<GroupInfo>().eq("gid", model.getGid())));

        // 业务判断处理
        if (model.isDismiss()) {
            StringBuilder sb = new StringBuilder(model.getMembers());
            sb.delete(0, model.getSenderUsername().length() + 1);
            String[] usernames = String.valueOf(sb).split(",");
            for (String username : usernames) {
                if (!"".equals(username)) {
                    model.setReceiverUsername(username);
                    noticeGroup.setReceiverUsername(username);
                    noticeGroupService.addNoticeRecord(noticeGroup);
                    updateRedisGroupNotice(username, noticeGroup);
                    simpMessagingTemplate.convertAndSendToUser(model.getReceiverUsername(), "/topic/notice/group", model);
                }
            }
        } else {
            if (model.isJoin()) {
                noticeGroup.setJoin(1);
                if (model.getConfirm() != null) {
                    if (model.isConfirm()) {
                        groupInfoService.joinGroup(model);
                    }
                    noticeGroup.setFlag(1);
                    noticeGroup.setConfirm(model.getConfirm());
                    noticeGroup.setFlagTime(model.getFlagTime());
                    noticeGroup.setUpdateTime(TimeFormat.stringToLocalDateTime(model.getSendTime()));
                    noticeGroupService.updateNoticeGroup(noticeGroup);
                } else {
                    model.setFlagTime(model.getSendTime());
                    noticeGroup.setFlagTime(model.getFlagTime());
                    noticeGroup.setFlag(null);
                    noticeGroup.setVerified(null);
                    noticeGroup.setConfirm(null);
                }
            } else if (model.isQuit()) {
                noticeGroup.setQuit(1);
            } else if (model.isForceQuit()) {
                noticeGroup.setForceQuit(1);
            }

            // 操作数据库和redis
            noticeGroupService.addNoticeRecord(noticeGroup);
            updateRedisGroupNotice(model.getReceiverUsername(), noticeGroup);
            simpMessagingTemplate.convertAndSendToUser(model.getReceiverUsername(), "/topic/notice/group", model);
        }

    }

    /**
     * 更新redis中的系统通知
     *
     * @param username
     * @param model
     */
    private void updateRedisServerNotice(String username, NoticeModel model) {
        String key = serverNoticeKey + "," + username + ",";
        List<NoticeModel> cacheList = redisCache.getCacheList(key);
        if (cacheList.isEmpty()) {
            cacheList = noticeServerService.selectByUsername(username);
        }
        cacheList.add(model);
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
