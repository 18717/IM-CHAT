package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.mapper.NoticeServerMapper;
import com.li2n.im_server.pojo.NoticeServer;
import com.li2n.im_server.service.INoticeServerService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 推送通知 服务实现类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Service
public class NoticeServerServiceImpl extends ServiceImpl<NoticeServerMapper, NoticeServer> implements INoticeServerService {

}
