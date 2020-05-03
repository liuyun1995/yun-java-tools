package com.liuyun.github.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: liuyun18
 * @Date: 2019/1/24 下午7:42
 */
public class ThreadLocalHolder {

    public static ThreadLocal<Map<String, Object>> local = new ThreadLocal();

    public static void put(String key, Object value) {
        Map<String, Object> map = local.get();
        if(map == null) {
            map = new ConcurrentHashMap(16);
        }
        map.put(key, value);
        local.set(map);
    }

    public static <T> T get(String key) {
        Map<String, Object> map = local.get();
        if(map == null) {
            return null;
        }
        return (T) map.get(key);
    }

    public static void clear() {
        local.remove();
    }

}
