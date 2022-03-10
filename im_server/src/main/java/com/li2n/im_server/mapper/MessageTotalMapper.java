package com.li2n.im_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.li2n.im_server.pojo.MessageTotal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 私聊消息汇总 Mapper 接口
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Mapper
@Repository
public interface MessageTotalMapper extends BaseMapper<MessageTotal> {

    /**
     * 根据msgKey查询指定聊天记录
     * @param sendUsername
     * @param receiveUsername
     * @return
     */
    List<MessageTotal> selectByMsgKey(@Param("send") String sendUsername, @Param("receive") String receiveUsername);

    /**
     * 查询所有的聊天记录
     *
     * @return
     */
    List<MessageTotal> selectAll();

    /**
     * 查询有关登录用户所有的消息记录
     *
     * @param username
     * @return
     */
    List<MessageTotal> selectByUsername(@Param("username") String username);

}
