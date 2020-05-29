package com.liuyun.github.utils;

import java.util.UUID;

/**
 * @author: lewis
 * @create: 2020/5/29 下午4:39
 */
public class IdUtils {

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public static String simpleUUID() {
        return randomUUID().replaceAll("-", "");
    }

}
