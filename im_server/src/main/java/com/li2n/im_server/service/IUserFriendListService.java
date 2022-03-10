package com.li2n.im_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.li2n.im_server.pojo.UserFriendList;
import com.li2n.im_server.pojo.UserInfo;
import com.li2n.im_server.pojo.model.FriendModel;

import java.util.List;

/**
 * <p>
 * 好友列表 服务类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
public interface IUserFriendListService extends IService<UserFriendList> {

    /**
     * 添加好友记录
     * @param model
     * @return true/false
     */
    Boolean addFriendRecord(FriendModel model);

    /**
     * 获取某个用户的好友列表
     * @param username
     * @return
     */
    List<UserInfo> selectFriendList(String username);

}
