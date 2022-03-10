package com.li2n.im_server.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Jwt工具类
 *
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-1-26 下午 10:51
 */

@Component
public class JwtTokenUtil {

    /**
     * 用户名
     */
    private static final String CLAIM_KEY_USERNAME = "sub";
    /**
     * jwt 创建时间
     */
    private static final String CLAIM_KEY_CREATED = "created";
    /**
     * jwt 密钥
     */
    @Value("${jwt.secret}")
    private String secret;
    /**
     * jwt 失效时间
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 根据用户信息生成Token
     *
     * @param userDetails
     * @author 一杯香梨
     * @date 2022-1-26 下午 11:01
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * 从token中获取登录用户名
     *
     * @param token
     * @author 一杯香梨
     * @date 2022-1-26 下午 11:06
     */
    public String getUserNameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFormToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 验证token是否有效
     *
     * @param token
     * @param userDetails
     * @return true-有效；false-失效
     * @author 一杯香梨
     * @date 2022-1-26 下午 11:15
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUserNameFromToken(token);
        int index = username.indexOf('-');
        username = username.substring(index + 1);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * 判断token是否可以被刷新
     *
     * @param token
     * @author 一杯香梨
     * @date 2022-1-26 下午 11:19
     */
    public boolean canRefresh(String token) {
        return !isTokenExpired(token);
    }

    /**
     * 刷新token
     *
     * @param token
     * @author 一杯香梨
     * @date 2022-1-26 下午 11:22
     */
    public String refreshToken(String token) {
        Claims claims = getClaimsFormToken(token);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * 从token获取荷载
     *
     * @param token
     * @author 一杯香梨
     * @date 2022-1-26 下午 11:13
     */
    private Claims getClaimsFormToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return claims;
    }

    /**
     * 根据荷载生成JWT TOKEN
     *
     * @param claims
     * @author 一杯香梨
     * @date 2022-1-26 下午 11:03
     */
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 生成token失效时间
     *
     * @author 一杯香梨
     * @date 2022-1-26 下午 11:04
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    /**
     * 判断token是否失效
     *
     * @param token
     * @return true-过期；false-尚未过期
     * @author 一杯香梨
     * @date 2022-1-26 下午 11:16
     */
    private boolean isTokenExpired(String token) {
        Date expireDate = getExpiredDateFromToken(token);
        return expireDate.before(new Date());
    }

    /**
     * 从token中获取过期时间
     *
     * @param token
     * @author 一杯香梨
     * @date 2022-1-26 下午 11:17
     */
    private Date getExpiredDateFromToken(String token) {
        return getClaimsFormToken(token).getExpiration();
    }

}
