package com.li2n.im_server.utils;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-2-19 下午 4:14
 */
public class CaptchaUtils {

    /**
     * 根据不同的功能生成验证码
     *
     * @param request
     * @param response
     * @param str
     * @param captchaType
     * @param defaultKaptcha
     * @param redisCache
     */
    public static void createCaptcha(HttpServletRequest request,
                                     HttpServletResponse response,
                                     String str,
                                     String captchaType,
                                     DefaultKaptcha defaultKaptcha,
                                     RedisCache redisCache) {
        // 定义response输出类型为image/jpeg类型
        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");
        // return a jpeg
        response.setContentType("image/jpeg");

        // 生成验证码
        // 获取验证码内容
        String text = defaultKaptcha.createText();

        // 得到用户名
        int index = str.indexOf("?time");
        String username = str.substring(0, index);
        // 请求时间
        String time = str.substring(index + "?time".length());
        // 将验证码放入redis中保存
        String redisKey = captchaType + username;
        redisCache.setCacheObject(redisKey, text);

        System.out.println("用户[" + username + "]在(" + time + ")请求的[" + captchaType + "]验证码：" + text);
        // 将验证码放入session中
        request.getSession().setAttribute("captcha", text);
        // 根据文本内容创建图形验证码
        BufferedImage image = defaultKaptcha.createImage(text);
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            ImageIO.write(image, "jpg", outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

}
