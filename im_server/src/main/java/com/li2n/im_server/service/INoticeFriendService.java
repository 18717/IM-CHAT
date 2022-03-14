package com.li2n.im_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.li2n.im_server.pojo.NoticeFriend;

/**
 * <p>
 * 好友通知 服务类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
public interface INoticeFriendService extends IService<NoticeFriend> {

    /**
     * 添加好友通知记录
     * @param noticeFriend 
     * @author 一杯香梨
     * @date 2022-3-13 下午 12:24
     */
    void addNoticeRecord(NoticeFriend noticeFriend);
}
