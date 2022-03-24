package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.mapper.GroupListMapper;
import com.li2n.im_server.pojo.GroupList;
import com.li2n.im_server.service.IGroupListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 群组列表 服务实现类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Service
public class GroupListServiceImpl extends ServiceImpl<GroupListMapper, GroupList> implements IGroupListService {

    @Autowired
    private GroupListMapper groupListMapper;

    /**
     * 获取用户的所有群gid
     *
     * @param username
     * @return
     */
    @Override
    public List<String> gidListByUsername(String username) {
        GroupList groupList = groupListMapper.selectOne(new QueryWrapper<GroupList>().eq("username", username));
        String[] gids = groupList.getGids().split(",");
        return new ArrayList<>(Arrays.asList(gids));
    }
}
