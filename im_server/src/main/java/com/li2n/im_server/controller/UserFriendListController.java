package com.li2n.im_server.controller;


import com.li2n.im_server.pojo.UserInfo;
import com.li2n.im_server.pojo.model.FriendModel;
import com.li2n.im_server.pojo.model.RespBeanModel;
import com.li2n.im_server.service.IUserFriendListService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * <p>
 * 好友列表 前端控制器
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@RestController
@RequestMapping("/friend")
public class UserFriendListController {

    @Autowired
    private IUserFriendListService friendService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @ApiOperation(value = "添加好友")
    @PostMapping("/add")
    public RespBeanModel friendAdd(FriendModel model) {
        Boolean result = friendService.addFriendRecord(model);
        if (result) {
            return RespBeanModel.success("成功添加" + model.getReceiveUsername() + "为好友");
        } else {
            return RespBeanModel.success("添加好友失败，用户好友位不足");
        }

    }

    @ApiOperation(value = "解除好友关系")
    @PostMapping("/del")
    public RespBeanModel friendDel(Principal principal, String username) {
        Integer flag = friendService.delFriend(principal, username);
        if (flag == 0) {
            return RespBeanModel.success("解除好友关系成功");
        } else if (flag == 1) {
            return RespBeanModel.error("非法请求，对方不是你的好友");
        } else if (flag == -1) {
            return RespBeanModel.error("非法请求，你不是对方的好友");
        } else {
            return RespBeanModel.error("非法请求");
        }
    }

    @ApiOperation(value = "获取用户好友列表")
    @GetMapping("/list")
    public List<UserInfo> friendList(String username) {
        return friendService.selectFriendList(username);
    }

}
