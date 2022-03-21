package com.li2n.im_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.li2n.im_server.pojo.GroupInfo;
import com.li2n.im_server.pojo.model.QueryGroupModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 群基本信息 Mapper 接口
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Mapper
@Repository
public interface GroupInfoMapper extends BaseMapper<GroupInfo> {

    /**
     * 分页查询群组
     *
     * @param page
     * @param queryGroupModel
     * @return
     */
    IPage<GroupInfo> selectListByPage(Page<GroupInfo> page, @Param("params") QueryGroupModel queryGroupModel);

    /**
     * 根据Gid更新群成员列表
     *  @param gid
     * @param groupInfo
     */
    void updateMemberList(@Param("gid") String gid, @Param("groupInfo") GroupInfo groupInfo);

}
