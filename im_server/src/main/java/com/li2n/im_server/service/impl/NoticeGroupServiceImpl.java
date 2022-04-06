package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.entity.NoticeGroup;
import com.li2n.im_server.mapper.NoticeGroupMapper;
import com.li2n.im_server.service.INoticeGroupService;
import com.li2n.im_server.utils.RedisCache;
import com.li2n.im_server.utils.TimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 好友通知 服务实现类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-21
 */
@Service
public class NoticeGroupServiceImpl extends ServiceImpl<NoticeGroupMapper, NoticeGroup> implements INoticeGroupService {

    @Value("${im-redis-key.notice.group}")
    private String groupNoticeKey;

    @Autowired
    private NoticeGroupMapper noticeGroupMapper;
    @Autowired
    private RedisCache redisCache;

    /**
     * 根据用户名查询群通知
     *
     * @param username
     * @return
     */
    @Override
    public List<NoticeGroup> selectByUsername(String username) {
        List<NoticeGroup> noticeGroups = new ArrayList<>();

        List<NoticeGroup> noticeGroupList =  noticeGroupMapper.selectListByReceiveUsername(username);
        if (noticeGroupList.isEmpty()) {
            return new ArrayList<>();
        }
        for (NoticeGroup noticeGroup : noticeGroupList) {
            noticeGroup.setTime(TimeFormat.localDateTimeToString(noticeGroup.getCreateTime()));
            noticeGroups.add(noticeGroup);
        }
        String key = groupNoticeKey + "," + username + ",";
        List<NoticeGroup> cacheList = redisCache.getCacheList(key);
        if (!cacheList.isEmpty()) {
            redisCache.deleteObject(key);
        }
        redisCache.setCacheList(key, noticeGroups);
        return noticeGroups;
    }

    /**
     * 将群通知添加到数据库中
     *
     * @param noticeGroup
     */
    @Override
    public void addNoticeRecord(NoticeGroup noticeGroup) {
        noticeGroupMapper.insert(noticeGroup);
    }

    /**
     * 更新群通知
     *
     * @param noticeGroup
     */
    @Override
    public void updateNoticeGroup(NoticeGroup noticeGroup) {
        noticeGroupMapper.updateNotice(noticeGroup, TimeFormat.stringToLocalDateTime(noticeGroup.getFlagTime()));
        selectByUsername(noticeGroup.getSender());
    }

    /**
     * 删除群通知
     *
     * @param gid
     */
    @Override
    public void delNoticeByGid(String gid) {
        noticeGroupMapper.delete(new QueryWrapper<NoticeGroup>().eq("gid", gid));
    }
}
