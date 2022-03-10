package com.li2n.im_server.utils;

import cn.hutool.core.util.RandomUtil;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * MUID 工具类
 *
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-02-01 13:50
 */
public class UID {

    /**
     * 时间戳
     */
    private static String timeMillis;
    /**
     * MAC地址
     */
    private static String mac;
    /**
     * 字符串
     */
    private static final String STR = "abcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * 根据传入的用户名随机生成12位数的uid 3-3-3-3
     *
     * @param username 用户名
     * @return uid
     */
    public static String uidGet12(String username) {
        init();
        StringBuilder sb = new StringBuilder();
        String[] uid = new String[4];
        uid[0] = createStr(timeMillis, 3);
        uid[1] = createStr(mac, 3);
        uid[2] = createStr(username, 3);
        uid[3] = createStr(STR, 3);
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < uid.length; i++) {
            int index = RandomUtil.randomInt(0, uid.length);
            if (sb2.lastIndexOf(String.valueOf(index)) == -1) {
                sb.append(uid[index]);
                sb2.append(index);
            } else {
                i--;
            }
        }
        return sb.toString();
    }

    /**
     * 根据传入的用户名随机生成16位数的uid 5-3-3-5
     *
     * @return uid
     */
    public static String uidGet16(String username) {
        init();
        StringBuilder sb = new StringBuilder();
        String[] uid = new String[4];
        uid[0] = createStr(timeMillis, 5);
        uid[1] = createStr(mac, 3);
        uid[2] = createStr(username, 3);
        uid[3] = createStr(STR, 5);
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < uid.length; i++) {
            int index = RandomUtil.randomInt(0, uid.length);
            if (sb2.lastIndexOf(String.valueOf(index)) == -1) {
                sb.append(uid[index]);
                sb2.append(index);
            } else {
                i--;
            }
        }
        return sb.toString();
    }

    /**
     * 获取MAC地址
     *
     * @return MAC字符串
     */
    private static String getLocalMAC() {
        // 获取地址
        byte[] mac = new byte[0];
        try {
            mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        StringBuffer sb = new StringBuffer("");
        for (byte b : mac) {

            //字节转换为整数
            int temp = b & 0xff;
            String str = Integer.toHexString(temp);
            if (str.length() == 1) {
                sb.append("0").append(str);
            } else {
                sb.append(str);
            }
        }
        return sb.toString().toLowerCase();
    }

    /**
     * 初始化时间戳和MAC
     */
    private static void init() {
        timeMillis = String.valueOf(System.currentTimeMillis());
        mac = getLocalMAC();
    }

    /**
     * 根据提供的字符串和长度生成随机字符串
     *
     * @param str    字符串范围
     * @param length 生成字符串的长度
     * @return
     */
    private static String createStr(String str, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int c = RandomUtil.randomInt(0, str.length());
            sb.append(str.charAt(c));
        }
        return sb.toString();
    }
}
