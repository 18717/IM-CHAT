package com.li2n.im_server.controller;

import com.li2n.im_server.pojo.UserInfo;
import com.li2n.im_server.pojo.model.*;
import com.li2n.im_server.service.IUserInfoService;
import com.li2n.im_server.utils.RedisCache;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

/**
 * 客户端控制器，有关客户端操作控制器
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-2-19 下午 7:59
 */
@RestController
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private IUserInfoService userService;
    @Autowired
    private RedisCache redisCache;

    @ApiOperation(value = "当前登录用户信息")
    @GetMapping("/login/info")
    public UserInfo getUserInfo(Principal principal) {
        if (principal == null) {
            return null;
        }
        String username = principal.getName();
        UserInfo user = redisCache.getCacheObject("login:c-" + username);
        user.setPassword(null);
        return user;
    }

    @ApiOperation(value = "注册用户")
    @PostMapping("/register")
    public RespBeanModel regUser(@RequestBody UserInfo userInfo, String code) {
        return userService.clientRegister(userInfo, code);
    }

    @ApiOperation(value = "修改密码")
    @PutMapping("/update/password")
    public RespBeanModel editPsw(@RequestBody EditPasswordModel model) {
        return userService.updatePassword(model);
    }

    @ApiOperation(value = "更新用户信息")
    @PutMapping("/update/user")
    public RespBeanModel updateUser(@RequestBody EditUserInfoModel model) {
        Boolean updateResult = userService.update(model, "login:c-");
        if (updateResult) {
            return RespBeanModel.success("更新数据成功");
        } else {
            return RespBeanModel.error("更新数据失败");
        }
    }

    @ApiOperation(value = "分页查询用户（不包含登录用户）")
    @GetMapping("/search/page")
    public RespPageBeanModel search(@RequestParam(defaultValue = "1") Integer currentPage,
                                    @RequestParam(defaultValue = "3") Integer size,
                                    Principal principal,
                                    QueryUserModel model) {
        return userService.selectUserListByPage(currentPage, size, principal, model, "client");
    }

    @Deprecated
    @ApiOperation(value = "普通查询用户")
    @GetMapping("/search/no-page")
    public List<UserInfo> search(Principal principal, QueryUserModel model) {
        return userService.selectUserList(principal, model);
    }

}
