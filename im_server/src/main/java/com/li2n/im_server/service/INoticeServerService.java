package com.li2n.im_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.li2n.im_server.entity.NoticeServer;

import java.util.List;

/**
 * <p>
 * 推送通知 服务类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
public interface INoticeServerService extends IService<NoticeServer> {

    /**
     * 添加通知
     * @param noticeServer
     */
    void insertNotice(NoticeServer noticeServer);

    /**
     * 获取用户的所有通知
     * @param username
     * @return
     */
    List<NoticeServer> selectByUsername(String username);

}
