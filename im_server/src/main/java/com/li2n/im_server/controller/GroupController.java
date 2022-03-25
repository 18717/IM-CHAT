package com.li2n.im_server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.li2n.im_server.pojo.GroupInfo;
import com.li2n.im_server.pojo.model.GroupModel;
import com.li2n.im_server.pojo.model.QueryGroupModel;
import com.li2n.im_server.pojo.model.RespBeanModel;
import com.li2n.im_server.pojo.model.RespPageBeanModel;
import com.li2n.im_server.service.IGroupInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * <p>
 * 群组 前端控制器
 * </p>
 *
 * @author Li2N
 * @since 2022-02-24
 */
@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private IGroupInfoService groupService;

    @ApiOperation(value = "建群")
    @PostMapping("/found")
    public RespBeanModel groupFound(@RequestBody GroupInfo groupInfo) {
        return groupService.foundGroup(groupInfo);
    }

    @ApiOperation(value = "搜群")
    @GetMapping("/search")
    public RespPageBeanModel groupSearch(@RequestParam(defaultValue = "1") Integer currentPage,
                                         @RequestParam(defaultValue = "3") Integer size,
                                         QueryGroupModel model) {
        return groupService.selectGroup(currentPage, size, model, "client");
    }

    @ApiOperation(value = "加群")
    @PostMapping("/add")
    public RespBeanModel groupAdd(@RequestBody GroupModel model) {
        return groupService.joinGroup(model);
    }

    @ApiOperation(value = "退群")
    @PostMapping("/quit")
    public RespBeanModel quit(String username, String gid) {
        int flag = groupService.quitGroup(username, gid);
        if (flag == 0) {
            return RespBeanModel.success("退群成功");
        } else if (flag == -1) {
            return RespBeanModel.error("退群失败，非本群成员");
        } else {
            return RespBeanModel.error("非法请求");
        }
    }

    @ApiOperation(value = "强制退群")
    @PostMapping("/force-quit")
    public RespBeanModel forceQuit(String username, String gid) {
        int flag = groupService.quitGroup(username, gid);
        if (flag == 0) {
            return RespBeanModel.success("强制退群成功");
        } else if (flag == -1) {
            return RespBeanModel.error("强制退群失败，非本群成员");
        } else {
            return RespBeanModel.error("非法请求");
        }
    }

    @ApiOperation(value = "修改群信息")
    @PostMapping("/edit-info")
    public RespBeanModel editInfo(@RequestBody GroupInfo groupInfo) {
        boolean result = groupService.updateGroupInfo(groupInfo);
        if (result) {
            return RespBeanModel.success("更新群信息成功，请刷新页面");
        } else {
            return RespBeanModel.error("数据异常，请刷新页面重试");
        }
    }

    @ApiOperation(value = "解散群")
    @PostMapping("/dismiss")
    public RespBeanModel dismiss(Principal principal, String gid) {
        if (principal == null) {
            return RespBeanModel.error("数据异常，请重新登录再重试");
        }
        int index = groupService.dismissGroup(principal, gid);
        if (index == 0) {
            return RespBeanModel.success("解散成功，大家好聚好散");
        } else if (index == -1 || index == 1) {
            return RespBeanModel.error("数据异常，请刷新页面后重试");
        }
        return RespBeanModel.error("非法请求");
    }

    @ApiOperation(value = "获取用户群列表")
    @GetMapping("/list")
    public List<GroupInfo> groupList(String username) {
        return groupService.selectGroupList(username);
    }

}
