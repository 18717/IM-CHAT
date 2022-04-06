package com.li2n.im_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.li2n.im_server.entity.GroupList;

import java.util.List;

/**
 * <p>
 * 群组列表 服务类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
public interface IGroupListService extends IService<GroupList> {

    /**
     * 获取用户的所有群gid
     * @param username
     * @return
     */
    List<String> gidListByUsername(String username);


    /**
     * 更新用户群列表信息
     * @param username
     * @param groupList
     */
    void updateGroupList(String username, GroupList groupList);
}
