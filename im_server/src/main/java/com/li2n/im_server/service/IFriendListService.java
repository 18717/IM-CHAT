package com.li2n.im_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.li2n.im_server.entity.FriendList;
import com.li2n.im_server.entity.User;

import java.security.Principal;
import java.util.List;

/**
 * <p>
 * 好友列表 服务类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
public interface IFriendListService extends IService<FriendList> {

    /**
     * 添加好友记录
     *
     * @param sender
     * @param receiver
     * @return true/false
     */
    Boolean addFriendRecord(String sender, String receiver);

    /**
     * 获取某个用户的好友列表
     * @param username
     * @return
     */
    List<User> selectFriendList(String username);

    /**
     * 解除好友关系
     * @param principal
     * @param sender
     * @param receiver
     * @return
     */
    Integer delFriend(Principal principal, String sender , String receiver);
}
