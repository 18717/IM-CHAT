package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.mapper.MessageGroupMapper;
import com.li2n.im_server.pojo.MessageGroup;
import com.li2n.im_server.service.IMessageGroupService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 群消息汇总 服务实现类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Service
public class MessageGroupServiceImpl extends ServiceImpl<MessageGroupMapper, MessageGroup> implements IMessageGroupService {

}
