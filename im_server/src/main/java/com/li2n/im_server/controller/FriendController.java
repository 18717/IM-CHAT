package com.li2n.im_server.controller;


import com.li2n.im_server.entity.User;
import com.li2n.im_server.service.IFriendListService;
import com.li2n.im_server.vo.ResponseResult;
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
public class FriendController {

    @Autowired
    private IFriendListService friendService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @ApiOperation(value = "添加好友")
    @PostMapping("/add")
    public ResponseResult friendAdd(String sender, String receiver) {
        Boolean result = friendService.addFriendRecord(sender, receiver);
        if (result) {
            return ResponseResult.success("成功添加" + receiver + "为好友");
        } else {
            return ResponseResult.success("添加好友失败，用户好友位不足");
        }

    }

    @ApiOperation(value = "解除好友关系")
    @PostMapping("/del")
    public ResponseResult friendDel(Principal principal, String username) {
        Integer flag = friendService.delFriend(principal, null, username);
        if (flag == 0) {
            return ResponseResult.success("解除好友关系成功");
        } else if (flag == 1) {
            return ResponseResult.error("非法请求，对方不是你的好友");
        } else if (flag == -1) {
            return ResponseResult.error("非法请求，你不是对方的好友");
        } else {
            return ResponseResult.error("非法请求");
        }
    }

    @ApiOperation(value = "获取用户好友列表")
    @GetMapping("/list")
    public List<User> friendList(String username) {
        return friendService.selectFriendList(username);
    }

}
