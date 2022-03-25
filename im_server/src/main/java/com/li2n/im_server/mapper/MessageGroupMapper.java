package com.li2n.im_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.li2n.im_server.pojo.MessageGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 群消息汇总 Mapper 接口
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Mapper
@Repository
public interface MessageGroupMapper extends BaseMapper<MessageGroup> {

    /**
     * 获取群聊天记录
     * @param gid
     * @return
     */
    List<MessageGroup> msgListByGid(@Param("gid") String gid);
}
