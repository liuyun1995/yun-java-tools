package com.liuyun.github.enums;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: liuyun18
 * @Date: 2018/11/22 下午4:05
 */
public enum WeekEnum {

    Monday(1, "周一"),
    Tuesday(2, "周二"),
    Wednesday(3, "周三"),
    Thursday(4, "周四"),
    Friday(5, "周五"),
    Saturday(6, "周六"),
    Sunday(7, "周日");

    public int code;
    public String week;

    WeekEnum(int code, String week) {
        this.code = code;
        this.week = week;
    }

    public static WeekEnum valueOf(int code) {
        if(code < 1 || code > 7) {
            throw new IllegalArgumentException("请传入正确的星期代号");
        }
        for (WeekEnum weekEnum : WeekEnum.values()) {
            if(weekEnum.code == code) {
                return weekEnum;
            }
        }
        return null;
    }

    /**
     * 获取星期几
     * @param date
     * @return
     */
    public WeekEnum getWeek(Date date) {
        if(date == null) {
            throw new IllegalArgumentException("传入的日期不能为空");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int w = calendar.get(Calendar.DAY_OF_WEEK);
        return WeekEnum.valueOf(w);
    }

    /**
     * 获取星期几
     * @param date
     * @return
     */
    public WeekEnum getWeek(String date, String format) throws Exception {
        if(date == null || "".equals(date)) {
            throw new IllegalArgumentException("传入的日期不能为空");
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return getWeek(sdf.parse(date));
    }

    /**
     * 是否是工作日
     * @param date
     * @return
     */
    public Boolean isWeekDay(String date, String format) throws Exception {
        return !isWeekend(date, format);
    }

    /**
     * 是否是周末
     * @param date
     * @return
     */
    public Boolean isWeekend(String date, String format) throws Exception {
        switch (getWeek(date, format)) {
            case Saturday: return Boolean.TRUE;
            case Sunday: return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}
