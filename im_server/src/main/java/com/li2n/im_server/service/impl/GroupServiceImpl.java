package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.entity.GroupInfo;
import com.li2n.im_server.entity.GroupList;
import com.li2n.im_server.entity.NoticeGroup;
import com.li2n.im_server.mapper.GroupListMapper;
import com.li2n.im_server.mapper.GroupMapper;
import com.li2n.im_server.service.IGroupListService;
import com.li2n.im_server.service.IGroupMessageService;
import com.li2n.im_server.service.IGroupService;
import com.li2n.im_server.service.INoticeGroupService;
import com.li2n.im_server.utils.RedisCache;
import com.li2n.im_server.utils.TimeFormat;
import com.li2n.im_server.utils.UID;
import com.li2n.im_server.vo.PageResponseResult;
import com.li2n.im_server.vo.QueryGroupVo;
import com.li2n.im_server.vo.ResponseGroupTip;
import com.li2n.im_server.vo.ResponseResult;
import com.qiniu.util.StringUtils;
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
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupInfo> implements IGroupService {

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
    private GroupMapper groupMapper;
    @Autowired
    private GroupListMapper groupListMapper;
    @Autowired
    private IGroupListService groupListService;
    @Autowired
    private IGroupMessageService messageGroupService;
    @Autowired
    private INoticeGroupService noticeGroupService;

    /**
     * 建群
     *
     * @param groupInfo
     * @return
     */
    @Override
    public ResponseResult foundGroup(GroupInfo groupInfo) {
        String captchaKey = foundGroupCaptcha + groupInfo.getLeader();
        String captcha = redisCache.getCacheObject(captchaKey);
        if ("".equals(groupInfo.getCode()) || !captcha.equalsIgnoreCase(groupInfo.getCode())) {
            return ResponseResult.error("验证码输入错误，请重新输入!");
        }
        redisCache.deleteObject(captchaKey);

        int foundNum = getFoundGroupNum(groupInfo.getLeader());
        int joinNum = getGroupNum(groupInfo.getLeader()) - foundNum;
        ResponseGroupTip tip = new ResponseGroupTip();
        tip.setFoundNum(foundNum);
        tip.setJoinNum(joinNum);
        tip.setRemainderFoundNum(GROUP_FOUND_MAX_NUM - foundNum);
        tip.setRemainderJoinNum(GROUP_JOIN_MAX_NUM - joinNum);

        if (foundNum > GROUP_FOUND_MAX_NUM || foundNum == GROUP_FOUND_MAX_NUM) {
            return ResponseResult.success(tip);
        }

        groupInfo.setGid(UID.uidGet12(groupInfo.getLeader()));
        groupInfo.setMembers(groupInfo.getLeader() + ",");
        groupInfo.setMemberNum(1);
        groupInfo.setCreateTime(TimeFormat.getLocalDateTime());
        groupInfo.setUpdateTime(TimeFormat.getLocalDateTime());
        groupMapper.insert(groupInfo);
        updateGroupList(groupInfo.getLeader(), groupInfo.getGid(), foundNum + 1, joinNum);

        tip.setGid(groupInfo.getGid());
        tip.setFoundNum(foundNum + 1);
        tip.setRemainderFoundNum(GROUP_FOUND_MAX_NUM - foundNum - 1);

        return ResponseResult.success("创建成功！", tip);
    }

    /**
     * 搜群（分页）
     *
     * @param currentPage     当前页
     * @param size            页大小
     * @param queryGroupVo 查询参数
     * @param path            来源路径
     * @return
     */
    @Override
    public PageResponseResult selectGroup(Integer currentPage, Integer size, QueryGroupVo queryGroupVo, String path) {

        IPage<GroupInfo> groupByPage;
        Page<GroupInfo> page = new Page<>(currentPage, size);

        if (StringUtils.isNullOrEmpty(queryGroupVo.getGid()) ||StringUtils.isNullOrEmpty(queryGroupVo.getGroupName())
                || StringUtils.isNullOrEmpty(queryGroupVo.getLeader())) {
            if ("server".equals(path)) {
                groupByPage = groupMapper.selectListByPage(page, queryGroupVo);
            } else if ("client".equals(path)) {
                groupByPage = groupMapper.selectListByPage(page, queryGroupVo);
            } else {
                return null;
            }
        } else {
            return null;
        }

        // 分页记录列表
        List<GroupInfo> groupInfoByPageRecords = groupByPage.getRecords();
        // 记录总数
        long total = groupByPage.getTotal();
        return new PageResponseResult(total, groupInfoByPageRecords);
    }

    /**
     * 加群
     *
     * @param noticeGroup
     * @return
     */
    @Override
    public ResponseResult joinGroup(NoticeGroup noticeGroup) {

        String oldGroupMembers = groupInfoByGid(noticeGroup.getGid()).getMembers();
        String[] memberArr = oldGroupMembers.split(",");
        for (String str : memberArr) {
            if (str.equals(noticeGroup.getReceiver())) {
                return ResponseResult.error("该用户已在群聊中。");
            }
        }

        int oldGroupMemberNum = getGroupMemberNum(noticeGroup.getGid());

        // 更新群成员
        StringBuilder groupMembers = new StringBuilder();
        groupMembers.append(oldGroupMembers);
        groupMembers = new StringBuilder();
        groupMembers.append(oldGroupMembers);
        if (GROUP_USER_MAX_NUM - oldGroupMemberNum != 1) {
            groupMembers.append(noticeGroup.getReceiver()).append(",");
        } else {
            groupMembers.append(noticeGroup.getReceiver());
        }
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setGid(noticeGroup.getGid());
        groupInfo.setMembers(groupMembers.toString());
        groupInfo.setMemberNum(oldGroupMemberNum + 1);
        groupInfo.setUpdateTime(LocalDateTime.now());

        // 更新入群用户的群列表
        String oldGids = getGroupListByUsername(noticeGroup.getReceiver()).getGids();
        int foundNum = getFoundGroupNum(noticeGroup.getReceiver());
        int joinNum = getGroupNum(noticeGroup.getReceiver()) - foundNum;

        groupMapper.updateMemberList(noticeGroup.getGid(), groupInfo);
        updateGroupList(noticeGroup.getReceiver(), noticeGroup.getGid(), foundNum, joinNum + 1);
        return ResponseResult.error("管理员已同意您的入群申请。");
    }

    /**
     * 获取用户的群列表
     *
     * @param username
     * @return
     */
    @Override
    public List<GroupInfo> selectGroupList(String username) {
        ArrayList<GroupInfo> groupInfoList = new ArrayList<>();
        String gids;
        try {
            gids = getGroupListByUsername(username).getGids();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if ("".equals(gids)) {
            return null;
        }
        for (String gid : gids.split(",")) {
            GroupInfo groupInfo = groupMapper.selectGroupInfo(gid);
            groupInfo.setMemberArr(getGroupMembers(gid));
            groupInfoList.add(groupInfo);
        }
        return groupInfoList;
    }

    /**
     * 获取某个用户创建的群聊个数
     *
     * @param leader
     * @return
     */
    private int getFoundGroupNum(String leader) {
        return groupMapper.selectList(new QueryWrapper<GroupInfo>().eq("leader", leader)).size();
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
        return groupMapper.selectOne(new QueryWrapper<GroupInfo>().eq("gid", gid));
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
        if (index == -1) { return -1; }
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
        if (index == -1) { return -1; }
        sb.delete(index, index + str.length());
        sb.delete(0, 1);
        groupList.setGids(String.valueOf(sb));
        groupList.setJoinNum(groupList.getJoinNum() - 1);
        groupList.setTotalNum(groupList.getTotalNum() - 1);

        groupMapper.updateMemberList(gid, groupInfo);
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
        if (!info.getLeader().equals(groupInfo.getLeader())) {
            return false;
        }
        info.setCreateTime(TimeFormat.stringToLocalDateTime(groupInfo.getTime()));
        info.setGroupName(groupInfo.getGroupName());
        info.setGroupDescribe(groupInfo.getGroupDescribe());
        return groupMapper.update(info, new QueryWrapper<GroupInfo>().eq("gid", groupInfo.getGid())) == 1;
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
        if (!groupInfo.getLeader().equals(username)) {
            return -1;
        }
        List<String> members = getGroupMembers(gid);
        if (!members.isEmpty()) {
            for (String member : members) {
                quitGroup(member, gid);
            }
        }
        quitGroup(username, gid);
        messageGroupService.delMsgByGid(gid);
        groupMapper.tombstonedByGid(gid);
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
