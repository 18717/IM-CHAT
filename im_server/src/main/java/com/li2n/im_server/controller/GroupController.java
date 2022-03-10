package com.li2n.im_server.controller;


import com.li2n.im_server.pojo.GroupInfo;
import com.li2n.im_server.pojo.model.GroupModel;
import com.li2n.im_server.pojo.model.QueryGroupModel;
import com.li2n.im_server.pojo.model.RespBeanModel;
import com.li2n.im_server.pojo.model.RespPageBeanModel;
import com.li2n.im_server.service.IGroupInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public RespBeanModel groupFound(GroupInfo groupInfo, String code) {
        return groupService.foundGroup(groupInfo, code);
    }

    @ApiOperation(value = "搜群")
    @PostMapping("/search")
    public RespPageBeanModel groupSearch(@RequestParam(defaultValue = "1") Integer currentPage,
                                         @RequestParam(defaultValue = "3") Integer size,
                                         QueryGroupModel model) {
        return groupService.selectGroup(currentPage, size, model, "client");
    }

    @ApiOperation(value = "加群")
    @PostMapping("/add")
    public RespBeanModel groupAdd(GroupModel model) {
        return groupService.joinGroup(model);
    }

    @ApiOperation(value = "获取用户群列表")
    @GetMapping("/list")
    public List<GroupInfo> groupList(String username) {
        return groupService.selectGroupList(username);
    }

}
