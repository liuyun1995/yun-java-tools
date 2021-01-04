package com.liuyun.github.security;

import java.security.MessageDigest;

/**
 * SHA-1加密(单向加密)
 * @Author: liuyun
 * @Date: 2018/11/28 下午4:53
 */
public class SHAUtils {

    /**
     * 单向加密
     * @param content
     * @return
     */
    public static String SHA(String content) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-512");
            byte[] bytes = sha.digest(content.getBytes("utf-8"));
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
