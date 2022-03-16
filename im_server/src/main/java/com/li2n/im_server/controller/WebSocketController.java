package com.li2n.im_server.controller;

import com.li2n.im_server.pojo.MessageTotal;
import com.li2n.im_server.pojo.NoticeFriend;
import com.li2n.im_server.pojo.NoticeServer;
import com.li2n.im_server.pojo.UserInfo;
import com.li2n.im_server.pojo.model.FriendModel;
import com.li2n.im_server.pojo.model.MessageModel;
import com.li2n.im_server.pojo.model.NoticeModel;
import com.li2n.im_server.service.IMessageOfflineService;
import com.li2n.im_server.service.IMessageTotalService;
import com.li2n.im_server.service.INoticeFriendService;
import com.li2n.im_server.service.INoticeServerService;
import com.li2n.im_server.utils.RedisCache;
import com.li2n.im_server.utils.TimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
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
                updateRedisNotice(username, model);
                simpMessagingTemplate.convertAndSendToUser(username, "/topic/notice/server", model);
            }
        } else {
            for (String username : usernames) {
                updateRedisNotice(username, model);
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
     * 发送添加/删除好友的消息和反馈
     *
     * @param model
     */
    @MessageMapping("/ws/friend/send")
    public void sendFriendRequest(FriendModel model) {

        UserInfo receiveUser = redisCache.getCacheObject("login:c-" + model.getReceiveUsername());
        NoticeFriend noticeFriend = new NoticeFriend();
        noticeFriend.setSendUsername(model.getSendUsername());
        noticeFriend.setReceiveUsername(model.getReceiveUsername());
        noticeFriend.setContent(model.getComment());
        noticeFriend.setCreateTime(TimeFormat.stringToLocalDateTime(model.getSendTime()));
        if (!Objects.isNull(receiveUser)) {
            noticeFriend.setOnline(1);
        } else {
            noticeFriend.setOnline(0);
        }
        if ("add".equals(model.getRequestType())) {
            noticeFriend.setAdd(1);
            noticeFriend.setDel(0);
            // TODO 添加好友操作
            if (model.getResult() == null) {
                System.out.println(model.getComment());
            } else if (model.getResult()) {
                System.out.println(model.getReceiveUsername() + "同意添加为好友");
            } else {
                System.out.println(model.getReceiveUsername() + "拒绝你的好友申请");
            }
        } else if ("del".equals(model.getRequestType())) {
            // TODO 删除好友操作
            noticeFriend.setAdd(0);
            noticeFriend.setDel(1);
        }

        noticeFriendService.addNoticeRecord(noticeFriend);
        simpMessagingTemplate.convertAndSendToUser(model.getReceiveUsername(), "/topic/notice/friend", model);

    }

    /**
     * 更新redis中的通知
     *
     * @param username
     * @param model
     */
    private void updateRedisNotice(String username, NoticeModel model) {
        String key = "notice-server:" + "," + username + ",";
        List<NoticeModel> cacheList = redisCache.getCacheList(key);
        if (cacheList.isEmpty()) {
            cacheList = noticeServerService.selectByUsername(username);
        }
        cacheList.add(model);
        redisCache.deleteObject(key);
        redisCache.setCacheList(key, cacheList);
    }

}
