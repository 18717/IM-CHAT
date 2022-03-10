package com.li2n.im_server.listener.Logout;

import com.li2n.im_server.pojo.UserInfo;
import org.springframework.context.ApplicationEvent;

/**
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-6 下午 12:54
 */
public class LogoutEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private UserInfo user;

    public LogoutEvent(Object source, UserInfo user) {
        super(source);
        this.user = user;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }
}
