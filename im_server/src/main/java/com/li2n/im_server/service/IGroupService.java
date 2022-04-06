package com.li2n.im_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.li2n.im_server.entity.GroupInfo;
import com.li2n.im_server.entity.NoticeGroup;
import com.li2n.im_server.vo.PageResponseResult;
import com.li2n.im_server.vo.QueryGroupVo;
import com.li2n.im_server.vo.ResponseResult;

import java.security.Principal;
import java.util.List;

/**
 * <p>
 * 群基本信息 服务类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
public interface IGroupService extends IService<GroupInfo> {

    /**
     * 建群
     * @param groupInfo
     * @return
     */
    ResponseResult foundGroup(GroupInfo groupInfo);

    /**
     * 搜群（分页）
     * @param currentPage 当前页
     * @param size 页大小
     * @param queryGroupVo 查询参数
     * @param path 来源路径
     * @return
     */
    PageResponseResult selectGroup(Integer currentPage, Integer size, QueryGroupVo queryGroupVo, String path);

    /**
     * 加群
     * @param noticeGroup
     * @return
     */
    ResponseResult joinGroup(NoticeGroup noticeGroup);

    /**
     * 根据Gid获取群聊信息
     * @param gid
     * @return
     */
    GroupInfo groupInfoByGid(String gid);


    /**
     * 获取用户的群列表
     * @param username
     * @return
     */
    List<GroupInfo> selectGroupList(String username);

    /**
     * 根据Gid获取群成员用户名
     * @param gid
     * @return
     */
    List<String> getGroupMembers(String gid);

    /**
     * 退出群聊
     * @param username
     * @param gid
     * @return
     */
    Integer quitGroup(String username, String gid);

    /**
     * 更新群信息
     * @param groupInfo
     * @return
     */
    boolean updateGroupInfo(GroupInfo groupInfo);

    /**
     * 解散群聊
     *
     * @param principal
     * @param gid
     * @return
     */
    Integer dismissGroup(Principal principal, String gid);
}
