package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.mapper.UserFriendListMapper;
import com.li2n.im_server.mapper.UserInfoMapper;
import com.li2n.im_server.pojo.UserFriendList;
import com.li2n.im_server.pojo.UserInfo;
import com.li2n.im_server.pojo.model.FriendModel;
import com.li2n.im_server.service.IUserFriendListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 好友列表 服务实现类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Service
public class UserFriendListServiceImpl extends ServiceImpl<UserFriendListMapper, UserFriendList> implements IUserFriendListService {

    /**
     * 最大好友个数
     */
    private static final int FRIEND_MAX_NUM = 5;

    @Autowired
    private UserFriendListMapper friendMapper;
    @Autowired
    private UserInfoMapper userMapper;

    /**
     * 添加好友记录
     *
     * @param model
     * @return true/false
     */
    @Override
    public Boolean addFriendRecord(FriendModel model) {
        // 用户名
        String receiverUsername = model.getReceiverUsername();
        String sendUsername = model.getSendUsername();

        // 从数据库中获取好友列表字符串
        String receiverFriends = friendListStr(model.getReceiverUsername());
        String sendFriends = friendListStr(model.getSendUsername());

        // 旧好友列表
        String[] sendSplit = new String[0];
        String[] receiverSplit = new String[0];

        // 判断用户的好友列表是否为空,  再将字符串进行分隔处理拿到好友用户名数组
        if ("".equals(receiverFriends)) {
            receiverFriends = "" + sendUsername;
        } else {
            sendSplit = friendListStr(receiverUsername).split(",");
            receiverFriends = receiverFriends + "," + sendUsername;
        }
        if ("".equals(sendFriends)) {
            sendFriends = "" + receiverUsername;
        } else {
            receiverSplit = friendListStr(sendUsername).split(",");
            sendFriends = sendFriends + "," + receiverUsername;
        }

        // 判断用户的好友是否已满，两个用户需要同时满足好友未满才能添加成功
        if (sendSplit.length < FRIEND_MAX_NUM && receiverSplit.length < FRIEND_MAX_NUM) {
            friendMapper.updateByUsername(receiverUsername, receiverFriends, LocalDateTime.now());
            friendMapper.updateByUsername(sendUsername, sendFriends, LocalDateTime.now());
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取某个用户的好友列表
     *
     * @param username
     * @return
     */
    @Override
    public List<UserInfo> selectFriendList(String username) {
        ArrayList<UserInfo> users = new ArrayList<>();
        String friends = friendListStr(username);
        if ("".equals(friends) || friends == null) {
            return users;
        }
        String[] split = friends.split(",");
        for (String str : split) {
            UserInfo user = userMapper.selectOne(str);
            user.setPassword(null);
            users.add(user);
        }
        return users;
    }


    /**
     * 获取某个用户的好友列表字符串
     *
     * @param username
     * @return
     */
    private String friendListStr(String username) {
        return friendMapper.selectOne(new QueryWrapper<UserFriendList>().eq("username", username)).getFriends();
    }

    /**
     * 获取某个用户的好友数
     *
     * @param username
     * @return
     */
    private int friendNum(String username) {
        return friendListStr(username).split(",").length;
    }

}
