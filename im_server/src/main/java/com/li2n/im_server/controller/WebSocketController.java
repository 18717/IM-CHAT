package com.li2n.im_server.controller;

import com.li2n.im_server.pojo.MessageTotal;
import com.li2n.im_server.pojo.UserInfo;
import com.li2n.im_server.pojo.model.FriendModel;
import com.li2n.im_server.service.IMessageOfflineService;
import com.li2n.im_server.service.IMessageTotalService;
import com.li2n.im_server.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

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

    @MessageMapping("/ws/client/chat")
    public void handleMsg(Authentication authentication, MessageTotal msg) {
        UserInfo user = (UserInfo) authentication.getPrincipal();
        String msgKey = user.getUsername() + "@" + msg.getReceiveUsername();

        msg.setMkey(msgKey);
        msg.setSendUsername(user.getUsername());

        String chatMsgTo = msg.getReceiveUsername();
        UserInfo toUser = redisCache.getCacheObject("login:c-" + chatMsgTo);
        if (!Objects.isNull(toUser)) {
            msg.setOnline(1);
            simpMessagingTemplate.convertAndSendToUser(msg.getReceiveUsername(), "/queue/chat", msg);
        } else {
            msg.setOnline(0);
            iMessageOfflineService.insertMsg(msg);
        }
        msgService.insertMsg(msg);
    }

    @MessageMapping("/ws/server/chat")
    public void radioMsg(MessageTotal msg) {
        msg.setReceiveUsername("server");
        simpMessagingTemplate.convertAndSend("/topic/chat", msg);
    }

    /**
     * 发送添加/删除好友的消息和反馈
     * @param model
     */
    @MessageMapping("/ws/friend/send")
    public void sendFriendRequest(FriendModel model) {

        System.out.println(model.toString());

        if ("add".equals(model.getRequestType())) {
            // 执行添加好友的操作：判断是否是添加好友的请求，判断是否是同意添加好友的反馈信息
            if (model.getResult() == null) {
                System.out.println(model.getReceiverUsername() + "收到好友请求");
                System.out.println(model.getContent());
                System.out.println(model.getSendUsername());
            } else if (model.getResult()) {
                System.out.println(model.getReceiverUsername() + "同意添加为好友");
            } else {
                System.out.println(model.getReceiverUsername() + "拒绝你的好友申请");
            }
        } else if ("del".equals(model.getRequestType())) {
            // 执行删除好友的操作
            System.out.println(model.getSendUsername() + "已将你从好友列表删除");
        }

        simpMessagingTemplate.convertAndSendToUser(model.getReceiverUsername(), "/topic/chat", model);


    }

}
