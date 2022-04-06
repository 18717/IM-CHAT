package com.li2n.im_server.controller;

import com.li2n.im_server.entity.User;
import com.li2n.im_server.service.IUserService;
import com.li2n.im_server.utils.RedisCache;
import com.li2n.im_server.vo.PageResponseResult;
import com.li2n.im_server.vo.QueryUserVo;
import com.li2n.im_server.vo.ResponseResult;
import com.li2n.im_server.vo.UserVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

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
    private IUserService userService;
    @Autowired
    private RedisCache redisCache;

    @ApiOperation(value = "登录用户信息")
    @GetMapping("/login/info")
    public User getUserInfo(Principal principal) {
        if (principal == null) {
            return null;
        }
        String username = principal.getName();
        User user = redisCache.getCacheObject(serverLoginKey + username);
        user.setPassword(null);
        return user;
    }

    @ApiOperation(value = "添加用户")
    @PostMapping("/create/user")
    public ResponseResult addUser(@RequestBody User user) {
        Boolean insertResult = userService.insert(user);
        if (insertResult) {
            return ResponseResult.success("添加用户成功");
        } else {
            return ResponseResult.error("该用户名已存在，添加失败");
        }
    }

    @ApiOperation(value = "更新用户信息")
    @PutMapping("/update/user")
    public ResponseResult updateUser(@RequestBody UserVo userVo) {
        Boolean updateResult = userService.updateUser(userVo, serverLoginKey);
        if (updateResult) {
            return ResponseResult.success("更新数据成功");
        } else {
            return ResponseResult.error("更新数据失败");
        }
    }

    @ApiOperation(value = "重置用户登录密码")
    @PutMapping("/update/password/{username}")
    public ResponseResult restPassword(@PathVariable String username) {
        Boolean result = userService.resetPassword(username);
        if (result) {
            return ResponseResult.success("密码重置成功");
        }
        return ResponseResult.error("密码重置失败");
    }

    @ApiOperation(value = "分页查询用户（不包含登录用户）")
    @GetMapping("/search/page")
    public PageResponseResult search(@RequestParam(defaultValue = "1") Integer currentPage,
                                     @RequestParam(defaultValue = "5") Integer size,
                                     Principal principal,
                                     QueryUserVo model) {
        return userService.selectUserListByPage(currentPage, size, principal, model, "server");
    }

    @ApiOperation(value = "根据参数查询用户（包含登录用户）")
    @GetMapping("/search/no-page")
    public List<User> search(Principal principal, QueryUserVo model) {
        return userService.selectUserList(principal, model);
    }

    @ApiOperation(value = "真实删除用户")
    @DeleteMapping("/delete/real/{username}")
    public ResponseResult realDeleteUser(@PathVariable String username) {
        Boolean deleteResult = userService.realDelete(username);
        if (deleteResult) {
            return ResponseResult.success("删除成功");
        }
        return ResponseResult.error("删除失败");
    }

    @ApiOperation("获取所有用户名（不包括禁用和注销的用户）")
    @GetMapping("/get/usernames")
    public List<String> getUsernameList() {
        return userService.usernameList();
    }
}
