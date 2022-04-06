package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.config.security.JwtTokenUtil;
import com.li2n.im_server.entity.*;
import com.li2n.im_server.listener.Login.LoginEvent;
import com.li2n.im_server.listener.Logout.LogoutEvent;
import com.li2n.im_server.mapper.*;
import com.li2n.im_server.service.IFriendListService;
import com.li2n.im_server.service.IGroupListService;
import com.li2n.im_server.service.IGroupService;
import com.li2n.im_server.service.IUserService;
import com.li2n.im_server.utils.RedisCache;
import com.li2n.im_server.utils.TimeFormat;
import com.li2n.im_server.utils.UID;
import com.li2n.im_server.vo.*;
import com.qiniu.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private FriendListMapper friendMapper;
    @Autowired
    private GroupListMapper groupListMapper;
    @Autowired
    private NoticeFriendMapper noticeFriendMapper;
    @Autowired
    private NoticeGroupMapper noticeGroupMapper;
    @Autowired
    private IGroupService groupService;
    @Autowired
    private IGroupListService groupListService;
    @Autowired
    private IFriendListService friendListService;
    @Autowired
    private ApplicationContext applicationContext;

    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Value("${im-redis-key.login.server}")
    private String serverLoginKey;
    @Value("${im-redis-key.login.client}")
    private String clientLoginKey;
    @Value("${im-redis-key.captcha.server}")
    private String serverLoginCaptcha;
    @Value("${im-redis-key.captcha.client}")
    private String clientCaptchaKey;
    @Value("${im-redis-key.usernames}")
    private String usernames;
    @Value("${im-redis-key.user}")
    private String userKey;

    /**
     * 客户端登录
     *
     * @param user 登录参数
     * @return
     */
    @Override
    public ResponseResult clientLogin(User user) {

        String username = user.getUsername();
        String password = user.getPassword();
        String code = user.getCode();

        // 从redisCache获取用户请求生成的验证码
        String key = clientCaptchaKey + username;
        String captcha = redisCache.getCacheObject(key);
        if ("".equals(code) || !captcha.equalsIgnoreCase(code)) {
            return ResponseResult.error("验证码输入错误，请重新输入!");
        }
        redisCache.deleteObject(key);

        User loginUser = userMapper.selectUserOne(username);

        if (!Objects.isNull(loginUser)) {
            if (loginUser.isEnabled()) {
                return ResponseResult.error("该用户已被禁用，请联系管理员");
            } else if (loginUser.ofLogin()) {
                return ResponseResult.error("该用户已经在线，无法登录");
            } else {
                if (!passwordEncoder.matches(password, loginUser.getPassword())) {
                    return ResponseResult.error("用户名或密码错误，请重新尝试");
                } else {
                    username = "c-" + username;
                    loginUser.setPassword(null);
                    loginUser.setLogin(1);
                    redisCache.setCacheObject("login:" + username, loginUser);
                    // 将c-username存入用户信息用于生成token，解析token的时候可以判断是客户端请求还是服务端请求
                    loginUser.setUsername(username);
                }
            }
        } else {
            return ResponseResult.error("该用户不存在");
        }

        // 生成token
        String token = jwtTokenUtil.generateToken(loginUser);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);

        LoginEvent loginEvent = new LoginEvent(this, loginUser);
        applicationContext.publishEvent(loginEvent);

        updateLoginCode(username, 1);

        return ResponseResult.success("登录成功", tokenMap);
    }

    /**
     * 客户端注销
     *
     * @return
     */
    @Override
    public ResponseResult clientLogout() {
        // 获取SecurityContextHolder中的用户名
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseResult.error("未登录，请先登录!");
        }
        updateLoginCode(user.getUsername(), 0);
        String redisKey = clientLoginKey + user.getUsername();
        redisCache.deleteObject(redisKey);
        updateLoginCode(user.getUsername(), 0);

        LogoutEvent logoutEvent = new LogoutEvent(this, user);
        applicationContext.publishEvent(logoutEvent);

        return ResponseResult.success("注销成功");
    }

    /**
     * 客户端注册
     *
     * @param user
     * @param code
     * @return
     */
    @Override
    public User clientRegister(User user, String code) {

        String encode = passwordEncoder.encode(user.getPassword());

        /*String key = "register-captcha:" + user.getUsername();
        String captcha = redisCache.getCacheObject(key);
        if ("".equals(code) || !captcha.equalsIgnoreCase(code)) {
            return ResponseResult.error("验证码输入错误，请重新输入!");
        }
        redisCache.deleteObject(key);*/

        if (selectOne(user.getUsername()) != null) {
            return null;
        }
        user.setPassword(encode);
        user.setUid(UID.uidGet16(user.getUsername()));
        user.setEnabled(0);
        user.setLogin(0);
        user.setAdmin(0);
        user.setDeleted(0);
        user.setCreateTime(TimeFormat.getLocalDateTime());
        user.setUpdateTime(TimeFormat.getLocalDateTime());
        int insert = userMapper.insert(user);
        if (insert != 1) {
            return null;
        }
        initFriendGroup(user.getUsername());
        user.setPassword(null);
        user.setRegisterTime(TimeFormat.localDateTimeToString(user.getCreateTime()));
        saveUsernamesToRedis();

        return user;
    }

    /**
     * 更新密码
     *
     * @param passwordVo 密码参数
     * @return
     */
    @Override
    public ResponseResult updatePassword(PasswordVo passwordVo) {
        String fresh = passwordEncoder.encode(passwordVo.getFresh());

        if (passwordVo.getOld().equals(passwordVo.getFresh())) {
            return ResponseResult.error("旧密码与新密码相同，修改失败");
        }
        // 从redis中获取登录用户的信息
        String key = clientLoginKey + passwordVo.getUsername();
        User user = redisCache.getCacheObject(key);

        if (!passwordEncoder.matches(passwordVo.getOld(), user.getPassword())) {
            return ResponseResult.error("旧密码不正确");
        } else {
            user.setPassword(fresh);
            int result = userMapper.update(user, new QueryWrapper<User>().eq("username", passwordVo.getUsername()));
            if (result != 1) {
                return ResponseResult.error("未知错误，修改失败");
            }
            redisCache.deleteObject(key);
            updateLoginCode(passwordVo.getUsername(), 0);

            return ResponseResult.success("修改成功，请重新登录");
        }
    }

    /**
     * 更新用户信息
     *
     * @param userVo
     * @param key
     * @return
     */
    @Override
    public Boolean updateUser(UserVo userVo, String key) {

        key = key + userVo.getUsername();

        User old = selectOne(userVo.getUsername());
        if (old == null) {
            return false;
        }
        old.setAvatar(userVo.getAvatar());
        old.setNickname(userVo.getNickname());
        old.setGender(userVo.getGender());
        old.setEmail(userVo.getEmail());

        if (!key.equals(clientLoginKey)) {
            if (userVo.isAdmin()) {
                old.setAdmin(1);
            } else {
                old.setAdmin(0);
            }
            if (userVo.isEnabled()) {
                old.setEnabled(1);
            } else {
                old.setEnabled(0);
            }
        }

        old.setUpdateTime(TimeFormat.getLocalDateTime());
        int updateResult = userMapper.update(old, new QueryWrapper<User>().eq("username", old.getUsername()));

        if (!Objects.isNull(redisCache.getCacheObject(key))) {
            redisCache.setCacheObject(key, selectOne(old.getUsername()));
        }
        return updateResult != 0;

    }

    /**
     * 分页查询用户（不包含登录用户）
     *
     * @param currentPage 当前页
     * @param size        分页大小
     * @param principal
     * @param model       查询参数
     * @param path        请求来源
     * @return
     */
    @Override
    public PageResponseResult selectUserListByPage(Integer currentPage, Integer size, Principal principal,
                                                   QueryUserVo model, String path) {
        String loginUser = principal.getName();
        IPage<User> userByPage;
        Page<User> page = new Page<>(currentPage, size);

        if ("server".equals(path)) {
            userByPage = userMapper.selectListByPage(page, model, loginUser);
        } else if ("client".equals(path)) {
            //  判断客户端传过来的参数是否为空
            if (StringUtils.isNullOrEmpty(model.getUid()) || StringUtils.isNullOrEmpty(model.getEmail())
                    || StringUtils.isNullOrEmpty(model.getNickname())) {
                userByPage = userMapper.selectListByPage(page, model, loginUser);
            } else {
                return null;
            }
        } else {
            return null;
        }

        List<User> userByPageRecords = userByPage.getRecords();
        long total = userByPage.getTotal();
        for (User user : userByPageRecords) {
            user.setPassword(null);
        }
        return new PageResponseResult(total, userByPageRecords);
    }

    /**
     * 查询所有用户
     *
     * @param principal
     * @param model     查询参数
     * @return
     */
    @Override
    public List<User> selectUserList(Principal principal, QueryUserVo model) {
        List<User> users = userMapper.selectUserList(model);
        for (User user : users) {
            user.setPassword(null);
        }
        return users;
    }

    /**
     * 服务端登录
     *
     * @param user
     * @return
     */
    @Override
    public ResponseResult serverLogin(User user) {

        String username = user.getUsername();
        String password = user.getPassword();
        String code = user.getCode();

        // 从redisCache获取用户请求生成的验证码
        String key = serverLoginCaptcha + username;
        String captcha = redisCache.getCacheObject(key);
        if ("".equals(code) || !captcha.equalsIgnoreCase(code)) {
            redisCache.deleteObject(key);
            return ResponseResult.error("验证码输入错误，请重新输入!");
        }
        redisCache.deleteObject(key);
        User loginUser = userMapper.selectUserOne(username);

        if (!Objects.isNull(loginUser)) {
            if (loginUser.ofAdmin()) {
                if (loginUser.isEnabled()) {
                    return ResponseResult.error("此用户已被禁用，请联系开发者");
                } else {
                    if (!passwordEncoder.matches(password, loginUser.getPassword())) {
                        return ResponseResult.error("用户名或密码不正确");
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
                return ResponseResult.error("请使用管理员账号登录");
            }

        } else {
            return ResponseResult.error("该用户不存在");
        }

        // 生成token
        String token = jwtTokenUtil.generateToken(loginUser);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        saveUsernamesToRedis();
        return ResponseResult.success("登录成功", tokenMap);
    }

    /**
     * 服务端注销
     *
     * @return
     */
    @Override
    public ResponseResult serverLogout() {
        // 获取SecurityContextHolder中的用户名
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        String redisKey = serverLoginKey + user.getUsername();
        redisCache.deleteObject(redisKey);
        return ResponseResult.success("注销成功");
    }

    /**
     * 添加用户
     *
     * @param userInfo
     * @return
     */
    @Override
    public Boolean insert(User userInfo) {
        User user = selectOne(userInfo.getUsername());
        if (user != null) {
            return false;
        }
        userInfo.setLogin(0);
        userInfo.setUid(UID.uidGet16(userInfo.getUsername()));
        userInfo.setPassword(passwordEncoder.encode("123456"));
        userInfo.setCreateTime(TimeFormat.getLocalDateTime());
        userInfo.setUpdateTime(TimeFormat.getLocalDateTime());
        userInfo.setDeleted(0);
        userMapper.insert(userInfo);
        initFriendGroup(userInfo.getUsername());
        saveUsernamesToRedis();
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
        User user = selectOne(username);
        if (user == null) {
            return false;
        }
        user.setPassword(passwordEncoder.encode("123456"));
        user.setUpdateTime(TimeFormat.getLocalDateTime());
        int result = userMapper.update(user, new QueryWrapper<User>().eq("username", username));
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
        User user = selectOne(username);
        if (user != null && user.ofLogin()) {
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
        List<String> gids = groupListService.gidListByUsername(username);
        if (gids != null) {
            for (String gid : gids) {
                groupService.quitGroup(username, gid);
            }
        }
        List<String> usernameList = usernameList();
        usernameList.remove(username);
        for (String receiver : usernameList) {
            friendListService.delFriend(null, username, receiver);
        }
        return userMapper.delete(new QueryWrapper<User>().eq("username", username)) != 0;
    }

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return
     */
    @Override
    public User selectOne(String username) {
        return userMapper.selectUserOne(username);
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
        return userMapper.updateLoginCode(username, code);
    }

    /**
     * 初始化用户好友列表和群组列表
     *
     * @param username
     */
    private void initFriendGroup(String username) {
        FriendList friendList = new FriendList();
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

    /**
     * 获取所有用户名（不包括禁用和注销的用户）
     *
     * @return
     */
    @Override
    public List<String> usernameList() {
        List<String> usernameList = redisCache.getCacheList(usernames);
        ;
        if (usernameList == null) {
            usernameList = saveUsernamesToRedis();
        }
        return usernameList;
    }

    /**
     * 从数据库中获取所有用户名（不包括禁用和注销的用户）并保存到redis中
     *
     * @return
     */
    private List<String> saveUsernamesToRedis() {
        List<String> usernameList = userMapper.selectUsernameAll();
        redisCache.deleteObject(usernames);
        redisCache.setCacheList(usernames, usernameList);
        return usernameList;
    }

    /**
     * 更新redis中的用户
     */
    @Override
    public void updateRedisUser() {
        redisCache.deleteObject(userKey);
        List<User> userList = userMapper.selectUserAll();
        for (User user : userList) {
            redisCache.setCacheObject(userKey + user.getUsername(), user);
        }
    }
}
