package com.li2n.im_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.li2n.im_server.entity.User;
import com.li2n.im_server.vo.*;

import java.security.Principal;
import java.util.List;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
public interface IUserService extends IService<User> {

    /**
     * 客户端登录
     * @param user 登录参数
     * @return
     */
    ResponseResult clientLogin(User user);

    /**
     * 客户端注销
     * @return
     */
    ResponseResult clientLogout();

    /**
     * 客户端注册
     * @param user
     * @param code
     * @return
     */
    User clientRegister(User user, String code);

    /**
     * 更新密码
     * @param model 密码参数
     * @return
     */
    ResponseResult updatePassword(PasswordVo model);

    /**
     * 更新用户信息
     * @param userVo
     * @param key
     * @return
     */
    Boolean updateUser(UserVo userVo, String key);

    /**
     * 根据参数查询用户（分页、不包含登录用户）
     * @param currentPage 当前页
     * @param size 分页大小
     * @param principal
     * @param model 查询参数
     * @param path 请求来源
     * @return
     */
    PageResponseResult selectUserListByPage(Integer currentPage, Integer size, Principal principal, QueryUserVo model, String path);

    /**
     * 根据参数查询用户（不包含登录用户）
     * @param principal
     * @param model 查询参数
     * @return
     */
    List<User> selectUserList(Principal principal, QueryUserVo model);

    /**
     * 服务端登录
     * @param user 登录参数
     * @return
     */
    ResponseResult serverLogin(User user);

    /**
     * 服务端注销
     * @return
     */
    ResponseResult serverLogout();

    /**
     * 添加用户
     * @param user
     * @return
     */
    Boolean insert(User user);

    /**
     * 重置用户密码
     * @param username 用户名
     * @return
     */
    Boolean resetPassword(String username);

    /**
     * 强制用户下线
     * @param username 用户名
     * @return
     */
    Boolean forceDisconnect(String username);

    /**
     * 真实删除用户
     * @param username 用户名
     * @return
     */
    Boolean realDelete(String username);

    /**
     * 查询某一个用户
     * @param username 用户名
     * @return
     */
    User selectOne(String username);

    /**
     * 更新用户状态
     * @param username 用户名
     * @param code 登录状态码
     * @return
     */
    Integer updateLoginCode(String username, Integer code);

    /**
     * 获取所有用户名（不包括禁用和注销的用户）
     * @return
     */
    List<String> usernameList();

    /**
     * 更新redis中的用户
     */
    void updateRedisUser();

}
