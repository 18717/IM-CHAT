package com.li2n.im_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.li2n.im_server.entity.NoticeGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 好友通知 Mapper 接口
 * </p>
 *
 * @author Li2N
 * @since 2022-03-21
 */
@Mapper
@Repository
public interface NoticeGroupMapper extends BaseMapper<NoticeGroup> {

    /**
     * 根据用户名查询群通知
     * @param username
     * @return
     */
    List<NoticeGroup> selectListByReceiveUsername(@Param("username") String username);

    /**
     * 更新群通知
     * @param noticeGroup
     * @param flagTime
     */
    void updateNotice(@Param("noticeGroup")NoticeGroup noticeGroup, @Param("flagTime")LocalDateTime flagTime);
}
