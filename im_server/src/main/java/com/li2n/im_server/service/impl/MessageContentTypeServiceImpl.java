package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.mapper.MessageContentTypeMapper;
import com.li2n.im_server.pojo.MessageContentType;
import com.li2n.im_server.service.IMessageContentTypeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息内容类型 服务实现类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Service
public class MessageContentTypeServiceImpl extends ServiceImpl<MessageContentTypeMapper, MessageContentType> implements IMessageContentTypeService {

}
