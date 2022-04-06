package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.entity.FriendList;
import com.li2n.im_server.entity.User;
import com.li2n.im_server.mapper.FriendListMapper;
import com.li2n.im_server.mapper.UserMapper;
import com.li2n.im_server.service.IFriendListService;
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
public class FriendListServiceImpl extends ServiceImpl<FriendListMapper, FriendList> implements IFriendListService {

    /**
     * 最大好友个数
     */
    private static final int FRIEND_MAX_NUM = 5;

    @Autowired
    private FriendListMapper friendMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisCache redisCache;

    /**
     * 添加好友记录
     *
     * @param sender
     * @param receiver
     * @return true/false
     */
    @Override
    public Boolean addFriendRecord(String sender, String receiver) {

        // 从数据库中获取好友列表字符串
        String receiverFriends = friendListStr(receiver);
        String senderFriends = friendListStr(sender);

        // 旧好友列表
        String[] senderSplit = new String[0];
        String[] receiverSplit = new String[0];

        // 判断用户的好友列表是否为空,  再将字符串进行分隔处理拿到好友用户名数组
        if ("".equals(receiverFriends)) {
            receiverFriends = "" + sender;
        } else {
            senderSplit = friendListStr(receiver).split(",");
            receiverFriends = receiverFriends + "," + sender;
        }
        if ("".equals(senderFriends)) {
            senderFriends = "" + receiver;
        } else {
            receiverSplit = friendListStr(sender).split(",");
            senderFriends = senderFriends + "," + receiver;
        }

        // 判断用户的好友是否已满，两个用户需要同时满足好友未满才能添加成功
        if (senderSplit.length < FRIEND_MAX_NUM && receiverSplit.length < FRIEND_MAX_NUM) {
            friendMapper.updateFriendsByUsername(receiver, receiverFriends, LocalDateTime.now());
            friendMapper.updateFriendsByUsername(sender, senderFriends, LocalDateTime.now());
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
    public List<User> selectFriendList(String username) {
        ArrayList<User> users = new ArrayList<>();
        String friends = friendListStr(username);
        if ("".equals(friends) || friends == null) {
            return users;
        }
        String[] split = friends.split(",");
        for (String str : split) {
            User user = userMapper.selectUserOne(str);
            user.setPassword(null);
            users.add(user);
        }
        return users;
    }

    /**
     * 解除好友关系
     *
     * @param principal
     * @param sender
     * @param receiver
     * @return 0 成功解除关系
     * 1 对方不是你的好友
     * -1 你不是对方好友
     * null 非法请求
     */
    @Override
    public Integer delFriend(Principal principal, String sender, String receiver) {
        String senderUsername;
        if (principal != null && principal.getName().equals(sender)) { senderUsername = sender; }
        else { return null; }
        String s1 = "," + senderUsername;
        String s2 = "," + receiver;
        StringBuilder sb1 = new StringBuilder("," + friendListStr(senderUsername));
        StringBuilder sb2 = new StringBuilder("," + friendListStr(receiver));

        int index1 = sb1.indexOf(s2);
        int index2 = sb2.indexOf(s1);

        if (index1 == -1) { return 1; }
        if (index2 == -1) { return -1; }

        sb1.delete(index1, index1 + receiver.length() + 1);
        sb1.delete(0, 1);
        sb2.delete(index2, index2 + senderUsername.length() + 1);
        sb2.delete(0, 1);

        friendMapper.updateFriendsByUsername(senderUsername, String.valueOf(sb1), LocalDateTime.now());
        friendMapper.updateFriendsByUsername(receiver, String.valueOf(sb2), LocalDateTime.now());

        return 0;
    }

    /**
     * 获取某个用户的好友列表字符串
     *
     * @param username
     * @return
     */
    private String friendListStr(String username) {
        String friends = null;
        try {
            friends = friendMapper.selectOne(new QueryWrapper<FriendList>().eq("username", username)).getFriends();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return friends;
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
