package com.li2n.im_server.listener.Login;

import com.li2n.im_server.entity.User;
import com.li2n.im_server.service.INoticeServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * 登录监听器
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-6 下午 12:52
 */
@Component
public class LoginListener implements ApplicationListener<LoginEvent> {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private INoticeServerService noticeServerService;

    @Override
    public void onApplicationEvent(LoginEvent event) {

        User user = event.getUser();
        System.out.println("系统提示：用户：[" + user.getNickname() + "] 已上线！");
        String username = user.getUsername();
        int index = username.indexOf('-');
        username = username.substring(index + 1);

        noticeServerService.selectByUsername(username);
    }
}
