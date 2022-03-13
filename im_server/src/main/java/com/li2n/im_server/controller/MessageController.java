package com.li2n.im_server.controller;

import com.li2n.im_server.pojo.model.MessageModel;
import com.li2n.im_server.service.IMessageTotalService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    private IMessageTotalService msgService;

    @ApiOperation(value = "获得有关登录用户所有的消息记录")
    @GetMapping("/history/username")
    public List<MessageModel> getLoginUserHistoryMsg(String username) {
        return msgService.selectByUsername(username);
    }

}
