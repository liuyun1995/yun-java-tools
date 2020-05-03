package com.liuyun.github.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: liuyun18
 * @Date: 2018/10/12 下午6:10
 */
public class DateUtils {

    public static final String yyyy_MM_dd = "yyyy-MM-dd";
    public static final String yyyy_MM_dd_HHmm = "yyyy-MM-dd HH:mm";
    public static final String yyyy_MM_dd_HHmmss = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";

    /**
     * 日期转字符串
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 字符串转日期
     * @param date
     * @param pattern
     * @return
     */
    public static Date parse(String date, String pattern) {
        if(date == null || "".equals(date)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(date);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
