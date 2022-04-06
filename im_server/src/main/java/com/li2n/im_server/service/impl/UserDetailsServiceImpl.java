package com.li2n.im_server.service.impl;

import com.li2n.im_server.entity.User;
import com.li2n.im_server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-2-19 下午 12:01
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectUserOne(username);
        //TODO 查询对应的权限信息


        return user;
    }
}
