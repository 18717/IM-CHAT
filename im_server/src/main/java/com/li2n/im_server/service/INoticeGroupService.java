package com.li2n.im_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.li2n.im_server.entity.NoticeGroup;

import java.util.List;

/**
 * <p>
 * 好友通知 服务类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-21
 */
public interface INoticeGroupService extends IService<NoticeGroup> {

    /**
     * 根据用户名查询群通知
     * @param username
     * @return
     */
    List<NoticeGroup> selectByUsername(String username);

    /**
     * 将群通知添加到数据库中
     * @param noticeGroup
     */
    void addNoticeRecord(NoticeGroup noticeGroup);

    /**
     * 更新群通知
     * @param noticeGroup
     */
    void updateNoticeGroup(NoticeGroup noticeGroup);

    /**
     * 删除群通知
     * @param gid
     */
    void delNoticeByGid(String gid);
}
