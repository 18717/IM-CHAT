package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.mapper.NoticeFriendMapper;
import com.li2n.im_server.pojo.NoticeFriend;
import com.li2n.im_server.service.INoticeFriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 好友通知 服务实现类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Service
public class NoticeFriendServiceImpl extends ServiceImpl<NoticeFriendMapper, NoticeFriend> implements INoticeFriendService {

    @Autowired
    private NoticeFriendMapper noticeFriendMapper;

    /**
     * 添加好友通知记录
     *
     * @param noticeFriend
     * @author 一杯香梨
     * @date 2022-3-13 下午 12:24
     */
    @Override
    public void addNoticeRecord(NoticeFriend noticeFriend) {
        noticeFriendMapper.insert(noticeFriend);
    }

}
