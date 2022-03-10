package com.li2n.im_server.listener.Logout;

import com.li2n.im_server.pojo.UserInfo;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-6 下午 12:55
 */
@Component
public class LogoutListener implements ApplicationListener<LogoutEvent> {
    @Override
    public void onApplicationEvent(LogoutEvent event) {
        UserInfo user = event.getUser();
        System.out.println("系统提示：用户：[" + user.getNickname() + "] 已下线！");
    }
}
