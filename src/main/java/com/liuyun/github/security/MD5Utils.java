package com.liuyun.github.security;

import java.security.MessageDigest;

/**
 * MD5加密(单向加密)
 * @Author: liuyun
 * @Date: 2018/11/28 下午4:53
 */
public class MD5Utils {

    /**
     * MD5加密
     * @param content
     * @return
     */
    public static String MD5(String content) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(content.getBytes("utf-8"));
            return toHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转成16进制字符串
     * @param bytes
     * @return
     */
    private static String toHex(byte[] bytes) {
        final char[] HEX = "0123456789ABCDEF".toCharArray();
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            builder.append(HEX[(bytes[i] >> 4) & 0x0f]);
            builder.append(HEX[bytes[i] & 0x0f]);
        }
        return builder.toString();
    }

}
