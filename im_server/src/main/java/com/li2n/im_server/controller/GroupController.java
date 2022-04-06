package com.li2n.im_server.controller;


import com.li2n.im_server.entity.GroupInfo;
import com.li2n.im_server.entity.NoticeGroup;
import com.li2n.im_server.service.IGroupService;
import com.li2n.im_server.vo.PageResponseResult;
import com.li2n.im_server.vo.QueryGroupVo;
import com.li2n.im_server.vo.ResponseResult;
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
    private IGroupService groupService;

    @ApiOperation(value = "建群")
    @PostMapping("/found")
    public ResponseResult groupFound(@RequestBody GroupInfo groupInfo) {
        return groupService.foundGroup(groupInfo);
    }

    @ApiOperation(value = "搜群")
    @GetMapping("/search")
    public PageResponseResult groupSearch(@RequestParam(defaultValue = "1") Integer currentPage,
                                          @RequestParam(defaultValue = "3") Integer size,
                                          QueryGroupVo model) {
        return groupService.selectGroup(currentPage, size, model, "client");
    }

    @ApiOperation(value = "加群")
    @PostMapping("/join")
    public ResponseResult groupAdd(@RequestBody NoticeGroup noticeGroup) {
        return groupService.joinGroup(noticeGroup);
    }

    @ApiOperation(value = "退群")
    @PostMapping("/quit")
    public ResponseResult quit(String username, String gid) {
        int flag = groupService.quitGroup(username, gid);
        if (flag == 0) {
            return ResponseResult.success("退群成功");
        } else if (flag == -1) {
            return ResponseResult.error("操作失败，非本群成员");
        } else {
            return ResponseResult.error("非法请求");
        }
    }

    @ApiOperation(value = "强制退群")
    @PostMapping("/force-quit")
    public ResponseResult forceQuit(String username, String gid) {
        int flag = groupService.quitGroup(username, gid);
        if (flag == 0) {
            return ResponseResult.success("强制退群成功");
        } else if (flag == -1) {
            return ResponseResult.error("强制退群失败，非本群成员");
        } else {
            return ResponseResult.error("非法请求");
        }
    }

    @ApiOperation(value = "修改群信息")
    @PostMapping("/edit")
    public ResponseResult editInfo(@RequestBody GroupInfo groupInfo) {
        boolean result = groupService.updateGroupInfo(groupInfo);
        if (result) {
            return ResponseResult.success("更新群信息成功，请刷新页面");
        } else {
            return ResponseResult.error("数据异常，请刷新页面重试");
        }
    }

    @ApiOperation(value = "解散群")
    @PostMapping("/dismiss")
    public ResponseResult dismiss(Principal principal, String gid) {
        if (principal == null) {
            return ResponseResult.error("数据异常，请重新登录再重试");
        }
        int index = groupService.dismissGroup(principal, gid);
        if (index == 0) {
            return ResponseResult.success("解散成功，大家好聚好散");
        } else if (index == -1 || index == 1) {
            return ResponseResult.error("数据异常，请刷新页面后重试");
        }
        return ResponseResult.error("非法请求");
    }

    @ApiOperation(value = "获取用户群列表")
    @GetMapping("/list")
    public List<GroupInfo> groupList(String username) {
        return groupService.selectGroupList(username);
    }

}
