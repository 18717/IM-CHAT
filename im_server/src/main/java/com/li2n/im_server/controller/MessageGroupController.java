package com.li2n.im_server.controller;


import com.li2n.im_server.pojo.MessageGroup;
import com.li2n.im_server.pojo.model.MessageModel;
import com.li2n.im_server.service.IMessageGroupService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 群消息汇总 前端控制器
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@RestController
@RequestMapping("/message-group")
public class MessageGroupController {

    @Autowired
    private IMessageGroupService messageGroupService;

    @ApiOperation(value = "获得登录用户的所有群消息记录")
    @GetMapping("/history/username")
    public Map<String, List<MessageGroup>> historyGroupMsg(String username) {
        return messageGroupService.msgListByUsername(username);
    }

}
