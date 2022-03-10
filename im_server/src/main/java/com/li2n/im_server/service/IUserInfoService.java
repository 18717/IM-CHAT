package com.li2n.im_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.li2n.im_server.pojo.UserInfo;
import com.li2n.im_server.pojo.model.*;

import javax.servlet.http.HttpServletRequest;
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
public interface IUserInfoService extends IService<UserInfo> {

    /**
     * 客户端登录
     * @param model 登录参数
     * @param request
     * @return
     */
    RespBeanModel clientLogin(LoginModel model, HttpServletRequest request);

    /**
     * 客户端注销
     * @return
     */
    RespBeanModel clientLogout();

    /**
     * 客户端注册
     * @param userInfo
     * @param code
     * @return
     */
    RespBeanModel clientRegister(UserInfo userInfo, String code);

    /**
     * 更新密码
     * @param model 密码参数
     * @return
     */
    RespBeanModel updatePassword(EditPasswordModel model);

    /**
     * 更新用户信息
     * @param model
     * @param key
     * @return
     */
    Boolean update(EditUserInfoModel model, String key);

    /**
     * 根据参数查询用户（分页、不包含登录用户）
     * @param currentPage 当前页
     * @param size 分页大小
     * @param principal
     * @param model 查询参数
     * @param path 请求来源
     * @return
     */
    RespPageBeanModel selectUserListByPage(Integer currentPage, Integer size, Principal principal, QueryUserModel model, String path);

    /**
     * 根据参数查询用户（不包含登录用户）
     * @param principal
     * @param model 查询参数
     * @return
     */
    List<UserInfo> selectUserList(Principal principal, QueryUserModel model);

    /**
     * 服务端登录
     * @param model 登录参数
     * @param request
     * @return
     */
    RespBeanModel serverLogin(LoginModel model, HttpServletRequest request);

    /**
     * 服务端注销
     * @return
     */
    RespBeanModel serverLogout();

    /**
     * 添加用户
     * @param userInfo
     * @param code
     * @return
     */
    Boolean insert(UserInfo userInfo, String code);

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
    UserInfo selectOne(String username);

    /**
     * 更新用户状态
     * @param username 用户名
     * @param code 登录状态码
     * @return
     */
    Integer updateLoginCode(String username, Integer code);

}
