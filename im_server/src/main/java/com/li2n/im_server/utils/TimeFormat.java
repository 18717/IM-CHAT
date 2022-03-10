package com.li2n.im_server.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;

import java.time.LocalDateTime;

/**
 * 时间格式化工具类
 *
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-02-03 13:45
 */
public class TimeFormat {

    /**
     * 获得当前时间
     * @return
     */
    public static LocalDateTime getLocalDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 将 LocalDateTime转化为字符串格式
     * @return
     */
    public static String getLocalDateTimeString() {
        LocalDateTime localDateTime = LocalDateTimeUtil.parse(LocalDateTime.now().toString());
        return LocalDateTimeUtil.format(localDateTime, DatePattern.NORM_DATETIME_PATTERN);
    }
}
