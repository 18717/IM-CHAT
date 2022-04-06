package com.li2n.im_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.li2n.im_server.entity.Message;

import java.util.List;

/**
 * <p>
 * 私聊消息汇总 服务类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
public interface IMessageService extends IService<Message> {

    /**
     * 添加消息记录
     * @param msg
     */
    void insertMsg(Message msg);

    /**
     * 根据提供的参数查询多条消息记录
     * @param column
     * @param value
     * @return
     */
    List<Message> selectByColumn(String column, String value);

    /**
     * 查询聊天记录
     * @param send 发送者用户名
     * @param receiver 接收者用户名
     * @return
     */
    List<Message> selectByMsgKey(String send, String receiver);

    /**
     * 获得所有的聊天记录
     * @return
     */
    List<Message> selectAll();

    /**
     * 查询有关登录用户所有的消息记录
     * @param username
     * @return
     */
    List<Message> selectByUsername(String username);

    /**
     * 删除有关的聊天记录
     * @param sender
     * @param receiver
     */
    void deleteMsgByKey(String sender, String receiver);

}
