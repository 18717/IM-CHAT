package com.li2n.im_server.controller;


import com.li2n.im_server.pojo.UserInfo;
import com.li2n.im_server.pojo.model.FriendModel;
import com.li2n.im_server.pojo.model.RespBeanModel;
import com.li2n.im_server.service.IUserFriendListService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @ApiOperation(value = "获取用户好友列表")
    @GetMapping("/list")
    public List<UserInfo> friendList(String username) {
        return friendService.selectFriendList(username);
    }

}
