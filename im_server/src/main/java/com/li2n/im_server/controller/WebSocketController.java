package com.li2n.im_server.controller;

import com.li2n.im_server.pojo.MessageTotal;
import com.li2n.im_server.pojo.NoticeFriend;
import com.li2n.im_server.pojo.NoticeServer;
import com.li2n.im_server.pojo.UserInfo;
import com.li2n.im_server.pojo.model.FriendModel;
import com.li2n.im_server.pojo.model.MessageModel;
import com.li2n.im_server.pojo.model.NoticeModel;
import com.li2n.im_server.service.*;
import com.li2n.im_server.utils.RedisCache;
import com.li2n.im_server.utils.TimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private IMessageTotalService msgService;
    @Autowired
    private IMessageOfflineService iMessageOfflineService;
    @Autowired
    private INoticeFriendService noticeFriendService;
    @Autowired
    private INoticeServerService noticeServerService;
    @Autowired
    private IUserFriendListService userFriendListService;

    @MessageMapping("/ws/client/chat")
    public void handleMsg(Authentication authentication, MessageModel model) {
        UserInfo user = (UserInfo) authentication.getPrincipal();
        String msgKey = user.getUsername() + "@" + model.getReceiveUsername();

        MessageTotal msg = new MessageTotal();

        msg.setMkey(msgKey);
        msg.setSendNickname(user.getNickname());
        msg.setSendUsername(user.getUsername());
        msg.setReceiveUsername(model.getReceiveUsername());
        msg.setMessageContentType(model.getMessageContentType());
        msg.setContent(model.getContent());
        msg.setFileUrl(model.getFileUrl());
        msg.setSendTime(TimeFormat.stringToLocalDateTime(model.getSendTime()));
        msg.setSelf(Integer.parseInt(model.getSelf()));

        model.setSendNickname(user.getNickname());
        model.setSendUsername(user.getUsername());

        UserInfo receiveUser = redisCache.getCacheObject("login:c-" + model.getReceiveUsername());
        if (!Objects.isNull(receiveUser)) {
            msg.setOnline(1);
            simpMessagingTemplate.convertAndSendToUser(model.getReceiveUsername(), "/queue/chat", model);
        } else {
            msg.setOnline(0);
            iMessageOfflineService.insertMsg(msg);
        }
        msgService.insertMsg(msg);
    }

    @MessageMapping("/ws/server/notice")
    public void serverToUser(NoticeModel model) {

        String receiveUsername = model.getReceiveUsername();
        List<String> usernames = redisCache.getCacheList("usernames");
        NoticeServer noticeServer = new NoticeServer();
        noticeServer.setTitle(model.getTitle());
        noticeServer.setSendUsername("server");
        noticeServer.setReceiveUsername(model.getReceiveUsername());
        noticeServer.setNoticeType(model.getPush());
        noticeServer.setFileUrl(model.getFileUrl());
        noticeServer.setContent(model.getContent());
        noticeServer.setContentType("text");
        noticeServer.setSendTime(TimeFormat.stringToLocalDateTime(model.getPushTime()));
        noticeServer.setPushNum(usernames.size());

        model.setPushTime(TimeFormat.localDateTimeToString(noticeServer.getSendTime()));
        model.setPushNum(usernames.size());

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
            for (String username : usernames) {
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

        UserInfo receiveUser = redisCache.getCacheObject("login:c-" + model.getReceiveUsername());
        NoticeFriend noticeFriend = new NoticeFriend();
        noticeFriend.setAvatarUrl(model.getAvatarUrl());
        noticeFriend.setSendNickname(model.getSendNickname());
        noticeFriend.setSendUsername(model.getSendUsername());
        noticeFriend.setReceiveUsername(model.getReceiveUsername());
        noticeFriend.setCreateTime(TimeFormat.stringToLocalDateTime(model.getSendTime()));
        noticeFriend.setSendTime(model.getSendTime());
        noticeFriend.setFlag(model.getFlag());
        // 是否在线
        if (!Objects.isNull(receiveUser)) {
            noticeFriend.setOnline(1);
        } else {
            noticeFriend.setOnline(0);
        }
        // 添加/删除
        if ("add".equals(model.getRequestType())) {
            noticeFriend.setContent(model.getComment());
            noticeFriend.setAdd(1);
            noticeFriend.setDel(0);
            if (model.getResult() != null) {
                if (model.getResult()) {
                    noticeFriend.setTitle("已同意申请");
                    noticeFriend.setContent(model.getComment());
                    noticeFriend.setConfirm(1);
                    noticeFriend.setVerified(1);
                    noticeFriend.setSendTime(model.getSendTime());
                    noticeFriend.setResult(model.getResult());
                    FriendModel friendModel = new FriendModel();
                    friendModel.setSendUsername(model.getReceiveUsername());
                    friendModel.setReceiveUsername(model.getSendUsername());
                    userFriendListService.addFriendRecord(friendModel);
                } else {
                    noticeFriend.setTitle("已拒绝申请");
                    noticeFriend.setContent(model.getComment());
                    noticeFriend.setSendTime(model.getSendTime());
                    noticeFriend.setResult(model.getResult());
                    noticeFriend.setConfirm(0);
                    noticeFriend.setVerified(1);
                }
                noticeFriend.setFlagTime(model.getFlagTime());
                noticeFriend.setUpdateTime(LocalDateTime.now());
                noticeFriendService.updateNoticeFriend(noticeFriend);
            } else {
                noticeFriend.setTitle(model.getContent());
                noticeFriend.setVerified(0);
            }
        } else if ("del".equals(model.getRequestType())) {
            noticeFriend.setAdd(0);
            noticeFriend.setDel(1);
            noticeFriend.setVerified(1);
            noticeFriend.setTitle(model.getContent());
        }
        noticeFriendService.addNoticeRecord(noticeFriend);
        updateRedisFriendNotice(model.getReceiveUsername(), noticeFriend);
        simpMessagingTemplate.convertAndSendToUser(model.getReceiveUsername(), "/topic/notice/friend", noticeFriend);

    }

    /**
     * 更新redis中的系统通知
     *
     * @param username
     * @param model
     */
    private void updateRedisServerNotice(String username, NoticeModel model) {
        String key = "notice-server:" + "," + username + ",";
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
        String key = "notice-friend:" + "," + username + ",";
        List<NoticeFriend> cacheList = redisCache.getCacheList(key);
        if (cacheList.isEmpty()) {
            noticeFriendService.selectByUsername(username);
        } else {
            cacheList.add(noticeFriend);
            redisCache.deleteObject(key);
            redisCache.setCacheList(key, cacheList);
        }
    }

}
