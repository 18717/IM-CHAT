package com.li2n.im_server.controller;

import com.li2n.im_server.entity.User;
import com.li2n.im_server.service.IUserService;
import com.li2n.im_server.vo.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录控制器
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-10 下午 11:47
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private IUserService userService;

    @ApiOperation(value = "客户端登录")
    @PostMapping("/client")
    public ResponseResult clientLogin(@RequestBody User user) {
        return userService.clientLogin(user);
    }

    @ApiOperation(value = "服务端登录")
    @PostMapping("/server")
    public ResponseResult serverLogin(@RequestBody User user) {
        return userService.serverLogin(user);
    }
}
