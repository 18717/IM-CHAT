package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.mapper.UserFriendListMapper;
import com.li2n.im_server.mapper.UserInfoMapper;
import com.li2n.im_server.pojo.UserFriendList;
import com.li2n.im_server.pojo.UserInfo;
import com.li2n.im_server.pojo.model.FriendModel;
import com.li2n.im_server.pojo.model.RespBeanModel;
import com.li2n.im_server.service.IUserFriendListService;
import com.li2n.im_server.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
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
    @Autowired
    private RedisCache redisCache;

    /**
     * 添加好友记录
     *
     * @param model
     * @return true/false
     */
    @Override
    public Boolean addFriendRecord(FriendModel model) {
        // 用户名
        String receiverUsername = model.getReceiveUsername();
        String sendUsername = model.getSendUsername();

        // 从数据库中获取好友列表字符串
        String receiverFriends = friendListStr(model.getReceiveUsername());
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
            friendMapper.updateFriendsByUsername(receiverUsername, receiverFriends, LocalDateTime.now());
            friendMapper.updateFriendsByUsername(sendUsername, sendFriends, LocalDateTime.now());
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
            UserInfo user = userMapper.selectUserOne(str);
            user.setPassword(null);
            users.add(user);
        }
        return users;
    }

    /**
     * 解除好友关系
     *
     * @param principal
     * @param username
     * @return
     */
    @Override
    public RespBeanModel delFriend(Principal principal, String username) {
        String senderUsername = principal.getName();
        String s1 = "," + senderUsername;
        String s2 = "," + username;
        StringBuilder sb1 = new StringBuilder("," + friendListStr(senderUsername));
        StringBuilder sb2 = new StringBuilder("," + friendListStr(username));

        int index1 = sb1.indexOf(s2);
        int index2 = sb2.indexOf(s1);

        if (index1 == -1) {
            return RespBeanModel.error("对方不是你的好友，操作失败！");
        }
        if (index2 == -1) {
            return RespBeanModel.error("你不是对方的好友，操作失败！");
        }

        sb1.delete(index1, index1 + username.length() + 1);
        sb1.delete(0, 1);
        sb2.delete(index2, index2 + senderUsername.length() + 1);
        sb2.delete(0, 1);

        friendMapper.updateFriendsByUsername(senderUsername, String.valueOf(sb1), LocalDateTime.now());
        friendMapper.updateFriendsByUsername(username, String.valueOf(sb2), LocalDateTime.now());

        return RespBeanModel.success("解除好友关系成功！");
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
