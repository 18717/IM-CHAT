package com.li2n.im_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.li2n.im_server.entity.NoticeFriend;
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
 * @since 2022-03-08
 */
@Mapper
@Repository
public interface NoticeFriendMapper extends BaseMapper<NoticeFriend> {

    /**
     * 获取用户的所有好友通知
     * @param username
     * @return
     */
    List<NoticeFriend> selectListByReceiveUsername(@Param("username") String username);

    /**
     * 更新好友通知
     * @param noticeFriend
     * @param flagTime
     */
    void updateNotice(@Param("noticeFriend") NoticeFriend noticeFriend, @Param("flagTime")LocalDateTime flagTime);
}
