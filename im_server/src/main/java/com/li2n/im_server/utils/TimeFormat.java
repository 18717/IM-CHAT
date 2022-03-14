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
     * 将 LocalDateTime转化为 yyyy-MM-dd HH:mm:ss 格式字符串
     * @param localDateTime
     * @return
     */
    public static String localDateTimeToString(LocalDateTime localDateTime) {
        return LocalDateTimeUtil.format(localDateTime, DatePattern.NORM_DATETIME_PATTERN);
    }

    /**
     * 解析 yyyy-MM-dd HH:mm:ss 格式时间
     * @param strTime
     * @return
     */
    public static LocalDateTime stringToLocalDateTime(String strTime) {
        return LocalDateTimeUtil.parse(strTime, DatePattern.NORM_DATETIME_PATTERN);
    }
}
