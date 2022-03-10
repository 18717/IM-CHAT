package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.mapper.GroupInfoMapper;
import com.li2n.im_server.mapper.GroupListMapper;
import com.li2n.im_server.pojo.GroupInfo;
import com.li2n.im_server.pojo.GroupList;
import com.li2n.im_server.pojo.model.*;
import com.li2n.im_server.service.IGroupInfoService;
import com.li2n.im_server.utils.RedisCache;
import com.li2n.im_server.utils.TimeFormat;
import com.li2n.im_server.utils.UID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 群基本信息 服务实现类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Service
public class GroupInfoServiceImpl extends ServiceImpl<GroupInfoMapper, GroupInfo> implements IGroupInfoService {

    /**
     * 每个用户可以创建的最大群聊数
     */
    private static final int GROUP_FOUND_MAX_NUM = 3;
    /**
     * 每个用户可以加入的最大群聊数
     */
    private static final int GROUP_JOIN_MAX_NUM = 5;
    /**
     * 每个群的最大人数
     */
    private static final int GROUP_USER_MAX_NUM = 10;

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private GroupInfoMapper groupInfoMapper;
    @Autowired
    private GroupListMapper groupListMapper;

    /**
     * 建群
     *
     * @param groupInfo
     * @param code
     * @return
     */
    @Override
    public RespBeanModel foundGroup(GroupInfo groupInfo, String code) {
        String captchaKey = "group-found:" + groupInfo.getMasterUsername();
  /*      String captcha = redisCache.getCacheObject(captchaKey);
        if ("".equals(code) || !captcha.equalsIgnoreCase(code)) {
            return RespBeanModel.error("验证码输入错误，请重新输入!");
        }
        redisCache.deleteObject(captchaKey);*/

        int foundNum = getFoundGroupNum(groupInfo.getMasterUsername());
        int joinNum = getGroupNum(groupInfo.getMasterUsername()) - foundNum;
        RespGroupTipModel tipModel = new RespGroupTipModel();
        tipModel.setFoundNum(foundNum);
        tipModel.setJoinNum(joinNum);
        tipModel.setRemainderFoundNum(GROUP_FOUND_MAX_NUM - foundNum);
        tipModel.setRemainderJoinNum(GROUP_JOIN_MAX_NUM - joinNum);

        if (foundNum > GROUP_FOUND_MAX_NUM || foundNum == GROUP_FOUND_MAX_NUM) {
            return RespBeanModel.error("创建失败！", tipModel);
        }

        groupInfo.setGid(UID.uidGet12(groupInfo.getMasterUsername()));
        groupInfo.setMembers(groupInfo.getMasterUsername() + ",");
        groupInfo.setMemberNum(1);
        groupInfo.setCreateTime(TimeFormat.getLocalDateTime());
        groupInfo.setUpdateTime(TimeFormat.getLocalDateTime());
        groupInfoMapper.insert(groupInfo);
        updateGroupList(groupInfo.getMasterUsername(), groupInfo.getGid(), foundNum + 1, joinNum);

        tipModel.setGid(groupInfo.getGid());
        tipModel.setFoundNum(foundNum + 1);
        tipModel.setRemainderFoundNum(GROUP_FOUND_MAX_NUM - foundNum - 1);

        return RespBeanModel.success("创建成功！", tipModel);
    }

    /**
     * 搜群（分页）
     *
     * @param currentPage     当前页
     * @param size            页大小
     * @param queryGroupModel 查询参数
     * @param path            来源路径
     * @return
     */
    @Override
    public RespPageBeanModel selectGroup(Integer currentPage, Integer size, QueryGroupModel queryGroupModel, String path) {

        IPage<GroupInfo> groupByPage;
        Page<GroupInfo> page = new Page<>(currentPage, size);

        if (queryGroupModel.getGid() != null || queryGroupModel.getGroupName() != null || queryGroupModel.getMasterUsername() != null) {
            if ("server".equals(path)) {
                groupByPage = groupInfoMapper.selectListByPage(page, queryGroupModel);
            } else if ("client".equals(path)) {
                groupByPage = groupInfoMapper.selectListByPage(page, queryGroupModel);
            } else {
                return new RespPageBeanModel(null);
            }
        } else {
            return new RespPageBeanModel(null);
        }

        // 分页记录列表
        List<GroupInfo> groupByPageRecords = groupByPage.getRecords();
        // 记录总数
        long total = groupByPage.getTotal();
        return new RespPageBeanModel(total, groupByPageRecords);
    }

    /**
     * 加群
     *
     * @param model
     * @return
     */
    @Override
    public RespBeanModel joinGroup(GroupModel model) {

        if (!model.isConfirm()) {
            //TODO 管理员拒绝加入群聊，向用户发出系统通知
            System.out.println("管理员拒绝加入群聊");
            return RespBeanModel.error("非常抱歉，管理员已拒绝您的入群申请。");
        }
        //TODO 管理员同意加入群聊，向用户发出系统通知
        String oldGroupMembers = getGroupByGid(model.getGid()).getMembers();
        String[] memberArr = oldGroupMembers.split(",");
        for (String str : memberArr) {
            if (str.equals(model.getSendUsername())) {
                return RespBeanModel.error("该用户已在群聊中。");
            }
        }

        int oldGroupMemberNum = getGroupMemberNum(model.getGid());

        // 更新群成员
        StringBuilder groupMembers = new StringBuilder();
        groupMembers.append(oldGroupMembers);
        groupMembers = new StringBuilder();
        groupMembers.append(oldGroupMembers);
        if (GROUP_USER_MAX_NUM - oldGroupMemberNum != 1) {
            groupMembers.append(model.getSendUsername()).append(",");
        } else {
            groupMembers.append(model.getSendUsername());
        }
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setGid(model.getGid());
        groupInfo.setMembers(groupMembers.toString());
        groupInfo.setMemberNum(oldGroupMemberNum + 1);
        groupInfo.setUpdateTime(LocalDateTime.now());

        // 更新入群用户的群列表
        String oldGids = getGroupListByUsername(model.getSendUsername()).getGids();
        int foundNum = getFoundGroupNum(model.getSendUsername());
        int joinNum = getGroupNum(model.getSendUsername()) - foundNum;

        groupInfoMapper.update(model.getGid(), groupInfo);
        updateGroupList(model.getSendUsername(), model.getGid(), foundNum, joinNum + 1);
        return RespBeanModel.error("管理员已同意您的入群申请。");
    }

    /**
     * 获取用户的群列表
     *
     * @param username
     * @return
     */
    @Override
    public List<GroupInfo> selectGroupList(String username) {

        ArrayList<GroupInfo> groups = new ArrayList<>();
        String gidStr = getGroupListByUsername(username).getGids();
        if ("".equals(gidStr)) {
            return null;
        }
        String[] gids = gidStr.split(",");
        for (String gid : gids) {
            groups.add(groupInfoMapper.selectOne(new QueryWrapper<GroupInfo>().eq("gid", gid)));
        }
        return groups;

    }

    /**
     * 获取某个用户创建的群聊个数
     *
     * @param masterUsername
     * @return
     */
    private int getFoundGroupNum(String masterUsername) {
        return groupInfoMapper.selectList(new QueryWrapper<GroupInfo>().eq("master_username", masterUsername)).size();
    }

    /**
     * 获取某个用户的所有群聊个数
     *
     * @param username
     * @return
     */
    private int getGroupNum(String username) {
        return getGroupListByUsername(username).getGids().split(",").length;
    }

    /**
     * 根据Gid获取群聊信息
     *
     * @param gid
     * @return
     */
    private GroupInfo getGroupByGid(String gid) {
        return groupInfoMapper.selectOne(new QueryWrapper<GroupInfo>().eq("gid", gid));
    }

    /**
     * 根据用户名获取用户群列表信息
     *
     * @param username
     * @return
     */
    private GroupList getGroupListByUsername(String username) {
        return groupListMapper.selectOne(new QueryWrapper<GroupList>().eq("username", username));
    }

    /**
     * 根据Gid获取群聊人数
     *
     * @param gid
     * @return
     */
    private int getGroupMemberNum(String gid) {
        return getGroupByGid(gid).getMembers().split(",").length;
    }

    /**
     * 根据用户名更新用户群组数据
     *
     * @param username
     */
    private void updateUserGroupInfo(String username) {

    }


    /**
     * 更新群组列表信息
     *
     * @param username
     * @param gid
     * @param foundNum
     * @param joinNum
     */
    private void updateGroupList(String username, String gid, int foundNum, int joinNum) {
        GroupList groupList = new GroupList();
        String oldGids = getGroupListByUsername(username).getGids();
        StringBuilder gids = new StringBuilder();
        gids.append(oldGids);
        if (joinNum != (GROUP_JOIN_MAX_NUM + GROUP_FOUND_MAX_NUM) - 1) {
            gids.append(gid).append(",");
        } else {
            gids.append(gid);
        }
        groupList.setGids(gids.toString());
        groupList.setFoundNum(foundNum);
        groupList.setJoinNum(joinNum);
        groupList.setTotalNum(foundNum + joinNum);
        groupList.setUpdateTime(LocalDateTime.now());
        groupListMapper.update(username, groupList);
    }

}
