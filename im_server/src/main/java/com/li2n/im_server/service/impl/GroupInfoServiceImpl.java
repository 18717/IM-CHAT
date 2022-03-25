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
import com.li2n.im_server.service.IGroupListService;
import com.li2n.im_server.service.IMessageGroupService;
import com.li2n.im_server.service.INoticeGroupService;
import com.li2n.im_server.utils.RedisCache;
import com.li2n.im_server.utils.TimeFormat;
import com.li2n.im_server.utils.UID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Value("${im-redis-key.captcha.group.found}")
    private String foundGroupCaptcha;

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private GroupInfoMapper groupInfoMapper;
    @Autowired
    private GroupListMapper groupListMapper;
    @Autowired
    private IGroupListService groupListService;
    @Autowired
    private IMessageGroupService messageGroupService;
    @Autowired
    private INoticeGroupService noticeGroupService;

    /**
     * 建群
     *
     * @param groupInfo
     * @return
     */
    @Override
    public RespBeanModel foundGroup(GroupInfo groupInfo) {
        String captchaKey = foundGroupCaptcha + groupInfo.getMasterUsername();
        String captcha = redisCache.getCacheObject(captchaKey);
        if ("".equals(groupInfo.getCode()) || !captcha.equalsIgnoreCase(groupInfo.getCode())) {
            return RespBeanModel.error("验证码输入错误，请重新输入!");
        }
        redisCache.deleteObject(captchaKey);

        int foundNum = getFoundGroupNum(groupInfo.getMasterUsername());
        int joinNum = getGroupNum(groupInfo.getMasterUsername()) - foundNum;
        RespGroupTipModel tipModel = new RespGroupTipModel();
        tipModel.setFoundNum(foundNum);
        tipModel.setJoinNum(joinNum);
        tipModel.setRemainderFoundNum(GROUP_FOUND_MAX_NUM - foundNum);
        tipModel.setRemainderJoinNum(GROUP_JOIN_MAX_NUM - joinNum);

        if (foundNum > GROUP_FOUND_MAX_NUM || foundNum == GROUP_FOUND_MAX_NUM) {
            return RespBeanModel.success(tipModel);
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

        String oldGroupMembers = groupInfoByGid(model.getGid()).getMembers();
        String[] memberArr = oldGroupMembers.split(",");
        for (String str : memberArr) {
            if (str.equals(model.getReceiverUsername())) {
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
            groupMembers.append(model.getReceiverUsername()).append(",");
        } else {
            groupMembers.append(model.getReceiverUsername());
        }
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setGid(model.getGid());
        groupInfo.setMembers(groupMembers.toString());
        groupInfo.setMemberNum(oldGroupMemberNum + 1);
        groupInfo.setUpdateTime(LocalDateTime.now());

        // 更新入群用户的群列表
        String oldGids = getGroupListByUsername(model.getReceiverUsername()).getGids();
        int foundNum = getFoundGroupNum(model.getReceiverUsername());
        int joinNum = getGroupNum(model.getReceiverUsername()) - foundNum;

        groupInfoMapper.updateMemberList(model.getGid(), groupInfo);
        updateGroupList(model.getReceiverUsername(), model.getGid(), foundNum, joinNum + 1);
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
            GroupInfo groupInfo = groupInfoMapper.selectGroupInfo(gid);
            groupInfo.setMemberArr(getGroupMembers(gid));
            groups.add(groupInfo);
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
    @Override
    public GroupInfo groupInfoByGid(String gid) {
        return groupInfoMapper.selectOne(new QueryWrapper<GroupInfo>().eq("gid", gid));
    }

    /**
     * 退出群聊
     *
     * @param username
     * @param gid
     * @return 0 退群成功
     * -1 非本群成员
     */
    @Override
    public Integer quitGroup(String username, String gid) {
        GroupInfo groupInfo = groupInfoByGid(gid);
        String members = groupInfo.getMembers();
        String str = "," + username;
        StringBuilder sb = new StringBuilder("," + members);
        int index = sb.indexOf(str);
        if (index == -1) {
            return -1;
        }
        sb.delete(index, index + str.length());
        sb.delete(0, 1);
        groupInfo.setMembers(String.valueOf(sb));
        groupInfo.setMemberNum(groupInfo.getMemberNum() - 1);
        groupInfo.setUpdateTime(LocalDateTime.now());

        GroupList groupList = getGroupListByUsername(username);
        String gids = groupList.getGids();
        str = "," + gid;
        sb = new StringBuilder("," + gids);
        index = sb.indexOf(str);
        if (index == -1) {
            return -1;
        }
        sb.delete(index, index + str.length());
        sb.delete(0, 1);
        groupList.setGids(String.valueOf(sb));
        groupList.setJoinNum(groupList.getJoinNum() - 1);
        groupList.setTotalNum(groupList.getTotalNum() - 1);

        groupInfoMapper.updateMemberList(gid, groupInfo);
        groupListService.updateGroupList(username, groupList);
        return 0;
    }

    /**
     * 更新群信息
     *
     * @param groupInfo
     * @return
     */
    @Override
    public boolean updateGroupInfo(GroupInfo groupInfo) {
        GroupInfo info = groupInfoByGid(groupInfo.getGid());
        if (!info.getMasterUsername().equals(groupInfo.getMasterUsername())) {
            return false;
        }
        info.setGroupName(groupInfo.getGroupName());
        info.setGroupDescribe(groupInfo.getGroupDescribe());
        return groupInfoMapper.update(info, new QueryWrapper<GroupInfo>().eq("gid", groupInfo.getGid())) == 1;
    }

    /**
     * 解散群聊
     *
     *
     * @param principal
     * @param gid
     * @return 0 解散成功
     *         1 群聊不存在
     *        -1 非群主请求
     *
     */
    @Override
    public Integer dismissGroup(Principal principal, String gid) {
        String username = principal.getName();
        GroupInfo groupInfo = groupInfoByGid(gid);
        if (groupInfo == null) {
            return 1;
        }
        if (!groupInfo.getMasterUsername().equals(username)) {
            return -1;
        }
        List<String> members = getGroupMembers(gid);
        if (!members.isEmpty()) {
            for (String member : members) {
                quitGroup(member, gid);
            }
        }
        quitGroup(username, gid);
        // noticeGroupService.delNoticeByGid(gid);
        messageGroupService.delMsgByGid(gid);
        groupInfoMapper.tombstonedByGid(gid);
        return 0;
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
        return groupInfoByGid(gid).getMembers().split(",").length;
    }


    /**
     * 根据Gid获取群成员用户名
     *
     * @param gid
     * @return
     */
    @Override
    public List<String> getGroupMembers(String gid) {
        String members = groupInfoByGid(gid).getMembers();
        int index = members.indexOf(",");

        if (members.length() < index + 2) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(members.substring(index + 1).split(",")));
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
        // TODO 需要修改为加群、建群时更新用户群列表信息
        groupList.setGids(gids.toString());
        groupList.setFoundNum(foundNum);
        groupList.setJoinNum(joinNum);
        groupList.setTotalNum(foundNum + joinNum);
        groupList.setUpdateTime(LocalDateTime.now());
        groupListMapper.updateGroupList(username, groupList);
    }

}
