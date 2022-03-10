package com.li2n.im_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.li2n.im_server.pojo.GroupInfo;
import com.li2n.im_server.pojo.model.GroupModel;
import com.li2n.im_server.pojo.model.QueryGroupModel;
import com.li2n.im_server.pojo.model.RespBeanModel;
import com.li2n.im_server.pojo.model.RespPageBeanModel;

import java.util.List;

/**
 * <p>
 * 群基本信息 服务类
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
public interface IGroupInfoService extends IService<GroupInfo> {

    /**
     * 建群
     * @param groupInfo
     * @param code
     * @return
     */
    RespBeanModel foundGroup(GroupInfo groupInfo, String code);

    /**
     * 搜群（分页）
     * @param currentPage 当前页
     * @param size 页大小
     * @param queryGroupModel 查询参数
     * @param path 来源路径
     * @return
     */
    RespPageBeanModel selectGroup(Integer currentPage, Integer size, QueryGroupModel queryGroupModel, String path);

    /**
     * 加群
     * @param model
     * @return
     */
    RespBeanModel joinGroup(GroupModel model);


    /**
     * 获取用户的群列表
     * @param username
     * @return
     */
    List<GroupInfo> selectGroupList(String username);

}
