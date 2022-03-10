package com.li2n.im_server.config;

import com.li2n.im_server.config.security.JwtTokenUtil;
import com.li2n.im_server.pojo.UserInfo;
import com.li2n.im_server.utils.RedisCache;
import com.qiniu.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Objects;

/**
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-3-6 下午 12:42
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private RedisCache redisCache;

    /**
     * 添加Endpoint，这样在网页可以通过websocket连接上服务，websocket服务地址，并且可以指定是否使用socketJS
     *
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] paths = {"/ws/server", "/ws/client"};
        String[] domains = {"http://localgost:8082", "http://localhost:8083"};
        registry.addEndpoint(paths).setAllowedOrigins(domains).withSockJS();
    }

    /**
     * 输入通道参数配置 (使用了jwt令牌需要配置)
     *
     * @param registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                // 判断是否为连接，如果是，需要获取token，并且设置用户对象
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Auth-Token");
                    if (!StringUtils.isNullOrEmpty(token)) {
                        String authToken = token.substring(tokenHead.length());
                        String username = jwtTokenUtil.getUserNameFromToken(authToken);
                        // token中存在用户名
                        if (!StringUtils.isNullOrEmpty(username)) {

                            // 从redis中获取到用户信息
                            String redisKey = "login:" + username;
                            UserInfo cacheUser = redisCache.getCacheObject(redisKey);

                            // 判断redis中是否存在该用户
                            if (Objects.isNull(cacheUser)) {
                                return null;
                            }

                            // 验证token是否有效
                            if (jwtTokenUtil.validateToken(authToken, cacheUser)) {
                                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken
                                        (cacheUser, null, null);
                                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                                accessor.setUser(authenticationToken);
                            }
                        }
                    }
                }
                return message;
            }
        });
    }

    /**
     * 配置消息代理
     *
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        String[] strings = {"/queue", "/topic"};
        registry.enableSimpleBroker(strings);
    }

}
