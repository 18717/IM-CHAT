package com.li2n.im_server.listener.Login;

import com.li2n.im_server.pojo.UserInfo;
import org.springframework.context.ApplicationEvent;

/**
 * 登录事件
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-6 下午 12:52
 */
public class LoginEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private UserInfo user;

    public LoginEvent(Object source, UserInfo user) {
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
