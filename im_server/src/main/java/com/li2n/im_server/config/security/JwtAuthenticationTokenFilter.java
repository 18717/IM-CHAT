package com.li2n.im_server.config.security;

import com.li2n.im_server.pojo.UserInfo;
import com.li2n.im_server.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * JWT 登录授权过滤器
 *
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-1-27 上午 11:27
 */

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private RedisCache redisCache;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 获取请求头的token
        String authHeader = request.getHeader(tokenHeader);
        // 存在token，就解析
        if (authHeader != null && !"".equals(authHeader) && authHeader.startsWith(tokenHead)) {
            // 获取token
            String authToken = authHeader.substring(tokenHead.length());
            String username = jwtTokenUtil.getUserNameFromToken(authToken);
            // 从redis中获取用户信息
            String redisKey = "login:" + username;
            UserInfo cacheUser = redisCache.getCacheObject(redisKey);
            if (Objects.isNull(cacheUser)) {
                filterChain.doFilter(request, response);
                return;
            }
            // 验证token是否有效
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtTokenUtil.validateToken(authToken, cacheUser)) {
                    // token有效，将用户认证信息存入SecurityContextHolder
                    UsernamePasswordAuthenticationToken authenticationToken1 = new UsernamePasswordAuthenticationToken(cacheUser, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken1);
                }
            }
        }
        // 放行
        filterChain.doFilter(request, response);
    }
}
