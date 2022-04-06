package com.li2n.im_server.controller;

import com.li2n.im_server.entity.GroupMessage;
import com.li2n.im_server.entity.Message;
import com.li2n.im_server.service.IGroupMessageService;
import com.li2n.im_server.service.IMessageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 消息 前端控制器
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-12 下午 6:31
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private IMessageService msgService;

    @Autowired
    private IGroupMessageService messageGroupService;

    @ApiOperation(value = "获取好友消息记录")
    @GetMapping("/friend")
    public List<Message> userHistoryMessage(String username) {
        return msgService.selectByUsername(username);
    }

    @ApiOperation(value = "获取群消息记录")
    @GetMapping("/group")
    public Map<String, List<GroupMessage>> groupHistoryMessage(String username) {
        return messageGroupService.msgListByUsername(username);
    }

}
