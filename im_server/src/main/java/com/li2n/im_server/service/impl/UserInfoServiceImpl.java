package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.config.security.JwtTokenUtil;
import com.li2n.im_server.listener.Login.LoginEvent;
import com.li2n.im_server.listener.Logout.LogoutEvent;
import com.li2n.im_server.mapper.GroupInfoMapper;
import com.li2n.im_server.mapper.GroupListMapper;
import com.li2n.im_server.mapper.UserFriendListMapper;
import com.li2n.im_server.mapper.UserInfoMapper;
import com.li2n.im_server.pojo.GroupInfo;
import com.li2n.im_server.pojo.GroupList;
import com.li2n.im_server.pojo.UserFriendList;
import com.li2n.im_server.pojo.UserInfo;
import com.li2n.im_server.pojo.model.*;
import com.li2n.im_server.service.IUserInfoService;
import com.li2n.im_server.utils.RedisCache;
import com.li2n.im_server.utils.TimeFormat;
import com.li2n.im_server.utils.UID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private UserInfoMapper userMapper;
    @Autowired
    private UserFriendListMapper friendMapper;
    @Autowired
    private GroupListMapper groupListMapper;
    @Autowired
    private GroupInfoMapper groupInfoMapper;
    @Autowired
    private ApplicationContext applicationContext;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    /**
     * 客户端登录
     *
     * @param model   登录参数
     * @param request
     * @return
     */
    @Override
    public RespBeanModel clientLogin(LoginModel model, HttpServletRequest request) {

        String username = model.getUsername();
        String password = model.getPassword();
        String code = model.getCode();

        // 从redisCache获取用户请求生成的验证码
        String key = "client-captcha:" + username;
        String captcha = redisCache.getCacheObject(key);
        if ("".equals(code) || !captcha.equalsIgnoreCase(code)) {
            return RespBeanModel.error("验证码输入错误，请重新输入!");
        }
        redisCache.deleteObject(key);

        UserInfo loginUser = (UserInfo) userDetailsService.loadUserByUsername(username);

        if (!Objects.isNull(loginUser)) {
            if (loginUser.isEnabled()) {
                return RespBeanModel.error("该用户已被封号，请联系管理员");
            } else if (loginUser.isLogin()) {
                return RespBeanModel.error("该用户已经在线，无法登录");
            } else {
                if (!passwordEncoder.matches(password, loginUser.getPassword())) {
                    return RespBeanModel.error("用户名或密码不正确");
                } else {
                    updateLoginCode(username, 1);
                    loginUser.setLogin(1);
                    userMapper.update(loginUser, new QueryWrapper<UserInfo>().eq("username", username));
                    loginUser = selectOne(username);
                    if (!loginUser.isLogin()) {
                        return RespBeanModel.error("未知错误，登录失败，请联系管理员");
                    }
                    username = "c-" + username;
                    redisCache.setCacheObject("login:" + username, loginUser);
                    loginUser.setUsername(username);
                    loginUser.setPassword(null);
                }
            }
        } else {
            return RespBeanModel.error("该用户不存在");
        }

        // 生成token
        String token = jwtTokenUtil.generateToken(loginUser);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);

        LoginEvent loginEvent = new LoginEvent(this, loginUser);
        applicationContext.publishEvent(loginEvent);

        return RespBeanModel.success("登录成功", tokenMap);
    }

    /**
     * 客户端注销
     *
     * @return
     */
    @Override
    public RespBeanModel clientLogout() {
        // 获取SecurityContextHolder中的用户名
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = (UserInfo) authentication.getPrincipal();
        if (user == null) {
            return RespBeanModel.error("未登录，请先登录!");
        }
        updateLoginCode(user.getUsername(), 0);
        String redisKey = "login:c-" + user.getUsername();
        redisCache.deleteObject(redisKey);
        updateLoginCode(user.getUsername(), 0);

        LogoutEvent logoutEvent = new LogoutEvent(this, user);
        applicationContext.publishEvent(logoutEvent);

        return RespBeanModel.success("注销成功");
    }

    /**
     * 客户端注册
     * @param userInfo
     * @param code
     * @return
     */
    @Override
    public RespBeanModel clientRegister(UserInfo userInfo, String code) {

        String encode = passwordEncoder.encode(userInfo.getPassword());

        /*String key = "register-captcha:" + userInfo.getUsername();
        String captcha = redisCache.getCacheObject(key);
        if ("".equals(code) || !captcha.equalsIgnoreCase(code)) {
            return RespBeanModel.error("验证码输入错误，请重新输入!");
        }
        redisCache.deleteObject(key);*/

        if (selectOne(userInfo.getUsername()) != null) {
            return null;
        }
        userInfo.setPassword(encode);
        userInfo.setUid(UID.uidGet16(userInfo.getUsername()));
        userInfo.setDisable(0);
        userInfo.setLogin(0);
        userInfo.setAdmin(0);
        userInfo.setDeleted(0);
        userInfo.setCreateTime(TimeFormat.getLocalDateTime());
        userInfo.setUpdateTime(TimeFormat.getLocalDateTime());
        int insert = userMapper.insert(userInfo);
        if (insert != 1) {
            return null;
        }
        initFriendGroup(userInfo.getUsername());
        userInfo.setPassword(null);
        return RespBeanModel.success("注册成功", userInfo);
    }

    /**
     * 更新密码
     *
     * @param model 密码参数
     * @return
     */
    @Override
    public RespBeanModel updatePassword(EditPasswordModel model) {
        String fresh = passwordEncoder.encode(model.getFresh());

        if (model.getOld().equals(model.getFresh())) {
            return RespBeanModel.error("旧密码与新密码相同，修改失败");
        }
        // 从redis中获取登录用户的信息
        String key = "login:c-" + model.getUsername();
        UserInfo user = redisCache.getCacheObject(key);

        if (!passwordEncoder.matches(model.getOld(), user.getPassword())) {
            return RespBeanModel.error("旧密码不正确");
        } else {
            user.setPassword(fresh);
            int result = userMapper.update(user, new QueryWrapper<UserInfo>().eq("username", model.getUsername()));
            if (result != 1) {
                return RespBeanModel.error("未知错误，修改失败");
            }
            redisCache.deleteObject(key);
            updateLoginCode(model.getUsername(), 0);

            return RespBeanModel.success("修改成功，请重新登录");
        }
    }

    /**
     * 更新用户信息
     *
     * @param model
     * @param key
     * @return
     */
    @Override
    public Boolean update(EditUserInfoModel model, String key) {

        key = key + model.getUsername();

        UserInfo user = selectOne(model.getUsername());
        if (user == null) {
            return false;
        }
        user.setAvatar(model.getAvatar());
        user.setNickname(model.getNickname());
        user.setGender(model.getGender());
        user.setEmail(model.getEmail());
        // TODO 有朝一日再优化吧
        if ("true".equals(model.getAdmin())) {
            user.setAdmin(1);
        }
        if ("true".equals(model.getDisable())) {
            user.setDisable(1);
        }
        user.setUpdateTime(TimeFormat.getLocalDateTime());
        int updateResult = userMapper.update(user, new QueryWrapper<UserInfo>().eq("username", model.getUsername()));

        if (!Objects.isNull(redisCache.getCacheObject(key))) {
            redisCache.setCacheObject(key, selectOne(model.getUsername()));
        }
        return updateResult != 0;

    }

    /**
     * 分页查询用户（不包含登录用户）
     *
     * @param currentPage 当前页
     * @param size        分页大小
     * @param principal
     * @param model      查询参数
     * @param path        请求来源
     * @return
     */
    @Override
    public RespPageBeanModel selectUserListByPage(Integer currentPage, Integer size, Principal principal, QueryUserModel model, String path) {

        String loginUser = principal.getName();
        IPage<UserInfo> userByPage;
        Page<UserInfo> page = new Page<>(currentPage, size);
        if ("server".equals(path)) {
            userByPage = userMapper.selectListByPage(page, model, loginUser);
        } else if ("client".equals(path)) {
            //  判断客户端传过来的参数是否为空
            if (model.getUid() != null || model.getEmail() != null || model.getNickname() != null) {
                userByPage = userMapper.selectListByPage(page, model, loginUser);
            } else {
                return new RespPageBeanModel(null);
            }
        } else {
            return new RespPageBeanModel(null);
        }

        List<UserInfo> userByPageRecords = userByPage.getRecords();
        long total = userByPage.getTotal();
        for (UserInfo user : userByPageRecords) {
            user.setPassword(null);
        }
        return new RespPageBeanModel(total, userByPageRecords);
    }

    /**
     * 查询所有用户
     *
     * @param principal
     * @param model    查询参数
     * @return
     */
    @Override
    public List<UserInfo> selectUserList(Principal principal, QueryUserModel model) {
        List<UserInfo> users = userMapper.selectList(model);
        for (UserInfo user : users) {
            user.setPassword(null);
        }
        return users;
    }

    /**
     * 服务端登录
     *
     * @param model
     * @param request
     * @return
     */
    @Override
    public RespBeanModel serverLogin(LoginModel model, HttpServletRequest request) {

        String username = model.getUsername();
        String password = model.getPassword();
        String code = model.getCode();

        // 从redisCache获取用户请求生成的验证码
        String key = "server-captcha:" + username;
        String captcha = redisCache.getCacheObject(key);
        if ("".equals(code) || !captcha.equalsIgnoreCase(code)) {
            redisCache.deleteObject(key);
            return RespBeanModel.error("验证码输入错误，请重新输入!");
        }
        redisCache.deleteObject(key);
        UserInfo loginUser = (UserInfo) userDetailsService.loadUserByUsername(username);

        if (!Objects.isNull(loginUser)) {
            if (loginUser.isAdmin()) {
                if (loginUser.isEnabled()) {
                    return RespBeanModel.error("此用户已被禁用，请联系开发者");
                } else {
                    if (!passwordEncoder.matches(password, loginUser.getPassword())) {
                        return RespBeanModel.error("用户名或密码不正确");
                    } else {
                        // 将用户信息存入redis，s-username作为服务端登录的key
                        username = "s-" + username;
                        redisCache.setCacheObject("login:" + username, loginUser);
                        // 将s-username存入用户信息用于生成token，解析token的时候可以判断是客户端请求还是服务端请求
                        loginUser.setUsername(username);
                        loginUser.setPassword(null);
                    }
                }
            } else {
                return RespBeanModel.error("请使用管理员账号登录");
            }

        } else {
            return RespBeanModel.error("该用户不存在");
        }

        // 生成token
        String token = jwtTokenUtil.generateToken(loginUser);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return RespBeanModel.success("登录成功", tokenMap);
    }

    /**
     * 服务端注销
     *
     * @return
     */
    @Override
    public RespBeanModel serverLogout() {
        // 获取SecurityContextHolder中的用户名
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = (UserInfo) authentication.getPrincipal();
        String redisKey = "login:s-" + user.getUsername();
        redisCache.deleteObject(redisKey);
        return RespBeanModel.success("注销成功");
    }

    /**
     * 添加用户
     * @param userInfo
     * @return
     */
    @Override
    public Boolean insert(UserInfo userInfo) {
        UserInfo user = selectOne(userInfo.getUsername());
        if (user != null) {
            return false;
        }
        userInfo.setLogin(0);
        userInfo.setUid(UID.uidGet16(userInfo.getUsername()));
        userInfo.setPassword(passwordEncoder.encode("123456"));
        userInfo.setCreateTime(TimeFormat.getLocalDateTime());
        userInfo.setUpdateTime(TimeFormat.getLocalDateTime());
        userMapper.insert(userInfo);
        initFriendGroup(userInfo.getUsername());
        return true;
    }

    /**
     * 重置用户密码
     *
     * @param username 用户名
     * @return
     */
    @Override
    public Boolean resetPassword(String username) {
        UserInfo user = selectOne(username);
        if (user == null) {
            return false;
        }
        user.setPassword(passwordEncoder.encode("123456"));
        user.setUpdateTime(TimeFormat.getLocalDateTime());
        int result = userMapper.update(user, new QueryWrapper<UserInfo>().eq("username", username));
        return result != 0;
    }

    /**
     * 强制用户下线
     *
     * @param username 用户名
     * @return
     */
    @Override
    public Boolean forceDisconnect(String username) {
        UserInfo user = selectOne(username);
        if (user != null && user.isLogin()) {
            redisCache.deleteObject("c-login:" + username);
            updateLoginCode(username, 0);
            return true;
        }
        return false;
    }

    /**
     * 真实删除用户
     *
     * @param username 用户名
     * @return
     */
    @Override
    public Boolean realDelete(String username) {
        friendMapper.delete(new QueryWrapper<UserFriendList>().eq("username", username));
        groupInfoMapper.delete(new QueryWrapper<GroupInfo>().eq("master_username", username));
        groupListMapper.delete(new QueryWrapper<GroupList>().eq("username", username));
        return userMapper.delete(new QueryWrapper<UserInfo>().eq("username", username)) != 0;
    }

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return
     */
    @Override
    public UserInfo selectOne(String username) {
        return userMapper.selectOne(username);
    }

    /**
     * 更新用户登录状态码
     *
     * @param username 用户名
     * @param code     登录状态码
     * @return
     */
    @Override
    public Integer updateLoginCode(String username, Integer code) {
        UserInfo user = selectOne(username);
        user.setLogin(code);
        return userMapper.update(user, new QueryWrapper<UserInfo>().eq("username", username));
    }

    /**
     * 初始化用户好友列表和群组列表
     *
     * @param username
     */
    private void initFriendGroup(String username) {
        UserFriendList friendList = new UserFriendList();
        friendList.setUsername(username);
        friendList.setFriends("");
        friendList.setCreateTime(LocalDateTime.now());
        friendList.setUpdateTime(LocalDateTime.now());
        friendMapper.insert(friendList);
        GroupList groupList = new GroupList();
        groupList.setUsername(username);
        groupList.setGids("");
        groupList.setCreateTime(LocalDateTime.now());
        groupList.setUpdateTime(LocalDateTime.now());
        groupListMapper.insert(groupList);
    }

}
