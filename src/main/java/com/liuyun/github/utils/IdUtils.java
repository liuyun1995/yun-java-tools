package com.liuyun.github.utils;

import java.util.UUID;

public class IdUtils {

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public static String simpleUUID() {
        return randomUUID().replaceAll("-", "");
    }

}
