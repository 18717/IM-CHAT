package com.li2n.im_server.controller;

import com.li2n.im_server.service.IUserService;
import com.li2n.im_server.vo.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注销登录控制器
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-10 下午 11:48
 */
@RestController
@RequestMapping("/logout")
public class LogoutController {

    @Autowired
    private IUserService userService;

    @ApiOperation(value = "客户端注销登录")
    @PostMapping("/client")
    public ResponseResult clientLogout() {
        return userService.clientLogout();
    }

    @ApiOperation(value = "服务端注销登录")
    @PostMapping("/server")
    public ResponseResult serverLogout() {
        return userService.serverLogout();
    }
}
