package com.li2n.im_server.controller;

import com.li2n.im_server.pojo.model.LoginModel;
import com.li2n.im_server.pojo.model.RespBeanModel;
import com.li2n.im_server.service.IUserInfoService;
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
    private IUserInfoService userService;

    @ApiOperation(value = "客户端登录")
    @PostMapping("/client")
    public RespBeanModel clientLogin(@RequestBody LoginModel model, HttpServletRequest request) {
        return userService.clientLogin(model, request);
    }

    @ApiOperation(value = "服务端登录")
    @PostMapping("/server")
    public RespBeanModel serverLogin(@RequestBody LoginModel model, HttpServletRequest request) {
        return userService.serverLogin(model, request);
    }
}
