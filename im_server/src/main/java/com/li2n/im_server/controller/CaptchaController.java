package com.li2n.im_server.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.li2n.im_server.utils.CaptchaUtils;
import com.li2n.im_server.utils.RedisCache;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 验证码
 *
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-01-29 01:49
 */
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Value("${im-redis-key.captcha.server}")
    private String serverLoginCaptcha;
    @Value("${im-redis-key.captcha.client}")
    private String clientCaptchaKey;
    @Value("${im-redis-key.captcha.register}")
    private String registerCaptchaKey;
    @Value("${im-redis-key.captcha.group.found}")
    private String foundGroupCaptcha;
    @Value("${im-redis-key.captcha.group.join}")
    private String joinGroupCaptcha;

    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @Autowired
    private RedisCache redisCache;

    @ApiOperation(value = "服务端登录验证码")
    @GetMapping(value = "/server/login", produces = "image/jpeg")
    public void serverCaptcha(HttpServletRequest request,
                              HttpServletResponse response,
                              String username) {
        CaptchaUtils.createCaptcha(request, response, username, serverLoginCaptcha, defaultKaptcha, redisCache);
    }

    @ApiOperation(value = "客户端登录验证码")
    @GetMapping(value = "/client/login", produces = "image/jpeg")
    public void clientCaptcha(HttpServletRequest request, HttpServletResponse response, String username) {
        CaptchaUtils.createCaptcha(request, response, username, clientCaptchaKey, defaultKaptcha, redisCache);
    }

    @ApiOperation(value = "客户端注册验证码")
    @GetMapping(value = "/client/register", produces = "image/jpeg")
    public void registerCaptcha(HttpServletRequest request, HttpServletResponse response, String username) {
        CaptchaUtils.createCaptcha(request, response, username, registerCaptchaKey, defaultKaptcha, redisCache);
    }

    @ApiOperation(value = "建群验证码")
    @GetMapping(value = "/group/found", produces = "image/jpeg")
    public void groupFoundCaptcha(HttpServletRequest request, HttpServletResponse response, String username) {
        CaptchaUtils.createCaptcha(request, response, username, foundGroupCaptcha, defaultKaptcha, redisCache);
    }

    @ApiOperation(value = "加群验证码")
    @GetMapping(value = "/group/add", produces = "image/jpeg")
    public void groupAddCaptcha(HttpServletRequest request, HttpServletResponse response, String username) {
        CaptchaUtils.createCaptcha(request, response, username, joinGroupCaptcha, defaultKaptcha, redisCache);
    }
}
