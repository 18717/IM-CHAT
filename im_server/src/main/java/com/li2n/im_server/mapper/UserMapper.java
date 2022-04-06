package com.li2n.im_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.li2n.im_server.entity.User;
import com.li2n.im_server.vo.QueryUserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 用户基本信息 Mapper 接口
 * </p>
 *
 * @author Li2N
 * @since 2022-03-08
 */
@Mapper
@Repository
public interface UserMapper extends BaseMapper<User> {

    /**
     * 分页查询用户（不包含登录用户）
     *
     * @param page 分页参数
     * @param params 查询参数
     * @param username 登录用户名
     * @return
     */
    IPage<User> selectListByPage(Page<User> page, @Param("params") QueryUserVo params, @Param("username") String username);

    /**
     * 普通查询用户
     *
     * @param params 查询参数
     * @return
     */
    List<User> selectUserList(@Param("params") QueryUserVo params);

    /**
     * 查询某个用户
     * @param username
     * @return
     */
    User selectUserOne(@Param("username") String username);

    /**
     * 获取所有的用户名（不包括禁用和注销的用户）
     * @return
     */
    @Select("select username from user where is_disable != 1 and is_deleted != 1")
    List<String> selectUsernameAll();

    /**
     * 获取所有的用户
     * @return
     */
    @Select("select * from user where is_disable != 1 and is_deleted != 1")
    List<User> selectUserAll();

    /**
     * 更新用户登录状态
     * @param username
     * @param code
     * @return
     */
    Integer updateLoginCode(@Param("username") String username,@Param("code") Integer code);
}
