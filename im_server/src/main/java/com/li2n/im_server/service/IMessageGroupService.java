package com.li2n.im_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.li2n.im_server.pojo.MessageGroup;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 群消息汇总 服务类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
public interface IMessageGroupService extends IService<MessageGroup> {

    /**
     * 添加群消息记录
     *
     * @param msg
     */
    void insertMsg(MessageGroup msg);

    /**
     * 查询用户的所有群消息记录
     *
     * @param username
     * @return
     */
    Map<String, List<MessageGroup>> msgListByUsername(String username);

    /**
     * 清空群聊天记录
     * @param gid
     */
    void delMsgByGid(String gid);
}
