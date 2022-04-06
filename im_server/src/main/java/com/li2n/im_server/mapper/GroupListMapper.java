package com.li2n.im_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.li2n.im_server.entity.GroupList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 群组列表 Mapper 接口
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Mapper
@Repository
public interface GroupListMapper extends BaseMapper<GroupList> {

    /**
     * 根据用户名更新用户群组列表
     * @param username
     * @param groupList
     */
    void updateGroupList(@Param("username") String username, @Param("groupList") GroupList groupList);

}
