package com.li2n.im_server.listener.Login;

import com.li2n.im_server.pojo.MessageOffline;
import com.li2n.im_server.pojo.UserInfo;
import com.li2n.im_server.service.IMessageOfflineService;
import com.li2n.im_server.service.INoticeServerService;
import com.li2n.im_server.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 登录监听器
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-6 下午 12:52
 */
@Component
public class LoginListener implements ApplicationListener<LoginEvent> {

    @Autowired
    private IMessageOfflineService iMessageOfflineService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private INoticeServerService noticeServerService;

    @Override
    public void onApplicationEvent(LoginEvent event) {

        UserInfo user = event.getUser();
        System.out.println("系统提示：用户：[" + user.getNickname() + "] 已上线！");
        String username = user.getUsername();
        int index = username.indexOf('-');
        username = username.substring(index + 1);
        List<MessageOffline> msgOfflineList = iMessageOfflineService.selectByColumn(username);

        if (msgOfflineList.size() != 0) {
            for (MessageOffline msgOffline : msgOfflineList) {
                simpMessagingTemplate.convertAndSendToUser(username, "/queue/chat", msgOffline);
            }
            iMessageOfflineService.clearOfflineMsg(username);
        }

        noticeServerService.selectByUsername(username);
    }
}
