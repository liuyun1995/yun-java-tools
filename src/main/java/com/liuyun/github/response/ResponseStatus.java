package com.liuyun.github.response;

/**
 * @Author: liuyun18
 * @Date: 2018/9/4 下午8:40
 */
public enum ResponseStatus {

    /** 请求成功 */
    SUCCESS(1000, "请求成功"),

    /** 无权访问 */
    ERROR_2000(2000, "无权访问"),

    /** 参数错误 */
    ERROR_2001(2001, "参数错误"),

    /** 内部错误 */
    ERROR_2002(2002, "内部错误"),

    /** 拒绝请求 */
    ERROR_2003(2003, "拒绝请求");

    public final Integer code;
    public final String text;

    ResponseStatus(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

}
