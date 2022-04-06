package com.li2n.im_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.li2n.im_server.entity.NoticeServer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 推送通知 Mapper 接口
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Mapper
@Repository
public interface NoticeServerMapper extends BaseMapper<NoticeServer> {


    /**
     * 获取用户的所有通知
     * @param username
     * @return
     */
    List<NoticeServer> selectListByReceiveUsername(@Param("username") String username);
}
