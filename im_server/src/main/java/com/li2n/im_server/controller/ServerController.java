package com.li2n.im_server.controller;

import com.li2n.im_server.pojo.UserInfo;
import com.li2n.im_server.pojo.model.*;
import com.li2n.im_server.service.IUserInfoService;
import com.li2n.im_server.utils.RedisCache;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

/**
 * 服务端控制器，有关服务端操作控制器
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-2-19 下午 8:02
 */
@RestController
@RequestMapping("/server")
public class ServerController {

    @Value("${im-redis-key.login.server}")
    private String serverLoginKey;

    @Autowired
    private IUserInfoService userService;
    @Autowired
    private RedisCache redisCache;

    @ApiOperation(value = "登录用户信息")
    @GetMapping("/login/info")
    public UserInfo getUserInfo(Principal principal) {
        if (principal == null) {
            return null;
        }
        String username = principal.getName();
        UserInfo user = redisCache.getCacheObject(serverLoginKey + username);
        user.setPassword(null);
        return user;
    }

    @ApiOperation(value = "添加用户")
    @PostMapping("/create/user")
    public RespBeanModel addUser(@RequestBody UserInfo userInfo) {
        Boolean insertResult = userService.insert(userInfo);
        if (insertResult) {
            return RespBeanModel.success("添加用户成功");
        } else {
            return RespBeanModel.error("该用户名已存在，添加失败");
        }
    }

    @ApiOperation(value = "更新用户信息")
    @PutMapping("/update/user")
    public RespBeanModel updateUser(@RequestBody EditUserInfoModel model) {
        Boolean updateResult = userService.update(model, serverLoginKey);
        if (updateResult) {
            return RespBeanModel.success("更新数据成功");
        } else {
            return RespBeanModel.error("更新数据失败");
        }
    }

    @ApiOperation(value = "重置用户登录密码")
    @PutMapping("/update/password/{username}")
    public RespBeanModel restPassword(@PathVariable String username) {
        Boolean result = userService.resetPassword(username);
        if (result) {
            return RespBeanModel.success("密码重置成功");
        }
        return RespBeanModel.error("密码重置失败");
    }

    @ApiOperation(value = "强制用户客户端退出登录")
    @PutMapping("/forced/offline/{username}")
    public RespBeanModel forcedUser(@PathVariable String username) {
        Boolean result = userService.forceDisconnect(username);
        if (result) {
            return RespBeanModel.success("强制下线成功");
        }
        return RespBeanModel.error("该用户未登录,强制下线失败");
    }

    @ApiOperation(value = "分页查询用户（不包含登录用户）")
    @GetMapping("/search/page")
    public RespPageBeanModel search(@RequestParam(defaultValue = "1") Integer currentPage,
                                    @RequestParam(defaultValue = "5") Integer size,
                                    Principal principal,
                                    QueryUserModel model) {
        return userService.selectUserListByPage(currentPage, size, principal, model, "server");
    }

    @ApiOperation(value = "根据参数查询用户（包含登录用户）")
    @GetMapping("/search/no-page")
    public List<UserInfo> search(Principal principal, QueryUserModel model) {
        return userService.selectUserList(principal, model);
    }

    @ApiOperation(value = "真实删除用户")
    @DeleteMapping("/delete/real/{username}")
    public RespBeanModel realDeleteUser(@PathVariable String username) {
        Boolean deleteResult = userService.realDelete(username);
        if (deleteResult) {
            return RespBeanModel.success("删除成功");
        }
        return RespBeanModel.error("删除失败");
    }

    @ApiOperation("获取所有用户名（不包括禁用和注销的用户）")
    @GetMapping("/get/usernames")
    public List<String> usernameList() {
        return userService.usernameList();
    }
}
