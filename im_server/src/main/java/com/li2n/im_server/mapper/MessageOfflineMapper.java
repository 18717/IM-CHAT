package com.li2n.im_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.li2n.im_server.pojo.MessageOffline;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 私聊离线消息 Mapper 接口
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Mapper
@Repository
public interface MessageOfflineMapper extends BaseMapper<MessageOffline> {

    /**
     * 根据用户名查询离线消息记录
     * @param username
     * @return
     */
    List<MessageOffline> selectList(@Param("username") String username);

    /**
     * 根据用户名删除离线消息记录
     * @param username
     */
    void deleteMsg(@Param("username") String username);
}
