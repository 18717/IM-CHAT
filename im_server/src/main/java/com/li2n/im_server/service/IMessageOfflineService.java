package com.li2n.im_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.li2n.im_server.pojo.MessageOffline;
import com.li2n.im_server.pojo.MessageTotal;

import java.util.List;

/**
 * <p>
 * 私聊离线消息 服务类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
public interface IMessageOfflineService extends IService<MessageOffline> {

    /**
     * 添加离线消息
     * @param msg 消息对象
     */
    void insertMsg(MessageTotal msg);

    /**
     * 根据提供的用户名查询多条离线消息记录
     * @param username
     * @return
     */
    List<MessageOffline> selectByColumn(String username);

    /**
     * 根据提供的参数清除离线聊天记录
     * @param username
     */
    void clearOfflineMsg(String username);

}
