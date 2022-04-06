package com.li2n.im_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.li2n.im_server.entity.Message;
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
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 根据msgKey查询指定聊天记录
     * @param sender
     * @param receiver
     * @return
     */
    List<Message> selectByMsgKey(@Param("sender") String sender, @Param("receiver") String receiver);

    /**
     * 查询所有的聊天记录
     *
     * @return
     */
    List<Message> selectAll();

    /**
     * 查询有关登录用户所有的消息记录
     *
     * @param username
     * @return
     */
    List<Message> selectByUsername(@Param("username") String username);

}
