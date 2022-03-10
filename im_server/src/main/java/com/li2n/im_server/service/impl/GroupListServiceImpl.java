package com.li2n.im_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li2n.im_server.mapper.GroupListMapper;
import com.li2n.im_server.pojo.GroupList;
import com.li2n.im_server.service.IGroupListService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 群组列表 服务实现类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Service
public class GroupListServiceImpl extends ServiceImpl<GroupListMapper, GroupList> implements IGroupListService {

}
