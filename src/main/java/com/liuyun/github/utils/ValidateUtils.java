package com.liuyun.github.utils;

/**
 * @Author: liuyun18
 * @Date: 2018/11/28 下午6:12
 */
public class ValidateUtils {

    /**
     * 电话号码
     */
    public static final String REGEX_PHONE = "(^(\\d{2,4}[-_－—]?)?\\d{3,8}([-_－—]?\\d{3,8})?([-_－—]?\\d{1,7})?$)|(^0?1[35]\\d{9}$)" ;

    /**
     * 手机号码
     */
    private static final String REGEX_MOBILE = "^(1(3|5|7|8)[0-9])\\\\d{8}$";

    /**
     * 电子邮箱
     */
    private static final String REGEX_EMAIL = ".+@.+\\.[a-z]+";

    /**
     * 字母数字
     */
    private static final String REGEX_ALPHANUMERIC = "[a-zA-Z0-9]+";

    /**
     * 身份证号码
     */
    private static final String REGEX_ID_CARD = "(\\d{14}|\\d{17})(\\d|x|X)";

    /**
     * 年龄(0~120)
     */
    private static final String REGEX_AGE = "^(?:[1-9][0-9]?|1[01][0-9]|120)$";

    /**
     * 是否电话号码
     * @param str
     * @return
     */
    private static boolean isPhone(String str) {
        return isRegexMatch(str, REGEX_PHONE);
    }

    /**
     * 是否手机号码
     * @param str
     * @return
     */
    private static boolean isMobile(String str) {
        return isRegexMatch(str, REGEX_MOBILE);
    }

    /**
     * 是否电子邮件
     * @param str
     * @return
     */
    public static boolean isEmail(String str) {
        return isRegexMatch(str, REGEX_EMAIL);
    }

    /**
     * 是否字母数字
     * @param str
     * @return
     */
    private static boolean isAlphanumeric(String str) {
        return isRegexMatch(str, REGEX_ALPHANUMERIC);
    }

    /**
     * 是否身份证号
     * @param str
     * @return
     */
    public static boolean isIdCard(String str) {
        return isRegexMatch(str, REGEX_ID_CARD);
    }

    /**
     * 是否年龄
     * @param str
     * @return
     */
    public static boolean isAge(String str) {
        return isRegexMatch(str, REGEX_AGE);
    }

    /**
     * 是否匹配表达式
     * @param str
     * @param regex
     * @return
     */
    public static boolean isRegexMatch(String str, String regex) {
        return str != null && str.matches(regex);
    }

}
