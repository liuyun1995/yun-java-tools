package com.liuyun.github.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DateUtils {

    private static Map<String, String> PATTERN_DATE_FORMAT_MAP = new HashMap();

    /**
     * 字符串转日期
     * @return
     */
    public static Date parse(String dateStr) {
        if(dateStr == null || "".equals(dateStr)) {
            return null;
        }
        for (String pattern : PATTERN_DATE_FORMAT_MAP.keySet()) {
            if(Pattern.compile(pattern).matcher(dateStr).matches()) {
                return parse(dateStr, PATTERN_DATE_FORMAT_MAP.get(pattern));
            }
        }
        return null;
    }

    /**
     * 字符串转日期
     * @return
     */
    public static Date parse(String dateStr, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(dateStr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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

    static {
        PATTERN_DATE_FORMAT_MAP.put("^([0-9]{4}-[0-9])$", "yyyy-MM");
        PATTERN_DATE_FORMAT_MAP.put("^([0-9]{4}/[0-9])$", "yyyy/MM");
        PATTERN_DATE_FORMAT_MAP.put("^([0-9]{4}-[0-9]{2})$", "yyyy-MM");
        PATTERN_DATE_FORMAT_MAP.put("^([0-9]{4}/[0-9]{2})$", "yyyy/MM");
        PATTERN_DATE_FORMAT_MAP.put("^([0-9]{4}-[0-9]{2}-[0-9]{2})$", "yyyy-MM-dd");
        PATTERN_DATE_FORMAT_MAP.put("^([0-9]{2}[:][0-9]{2})$", "HH:mm");
        PATTERN_DATE_FORMAT_MAP.put("^([0-9]{2}[:][0-9]{2}[:][0-9]{2})$", "HH:mm:ss");
        PATTERN_DATE_FORMAT_MAP.put("^([0-9]{4}-[0-9]{2}-[0-9]{2}[ ][0-9]{2}[:][0-9]{2})$", "yyyy-MM-dd HH:mm");
        PATTERN_DATE_FORMAT_MAP.put("^([0-9]{4}-[0-9]{2}-[0-9]{2}[ ][0-9]{2}[:][0-9]{2}[:][0-9]{2})$", "yyyy-MM-dd HH:mm:ss");
        PATTERN_DATE_FORMAT_MAP.put("^([0-9]{4}-[0-9]{2}-[0-9]{2}[ ][0-9]{2}[:][0-9]{2}[:][0-9]{2}\\.[0-9]{1})$", "yyyy-MM-dd HH:mm:ss");
        PATTERN_DATE_FORMAT_MAP.put("^([0-9]{4}-[0-9]{2}-[0-9]{2}[T][0-9]{2}[:][0-9]{2}[:][0-9]{2}\\.[0-9]{3}\\+[0-9]{4})$", "yyyy-MM-dd'T'HH:mm:ss.SSSSZZ");
    }

}
