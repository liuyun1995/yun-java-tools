package com.liuyun.github.utils;

import java.math.BigDecimal;

public class MoneyUtil {

    /** 默认小数位 */
    private static final int DEFAULT_DECIMAL = 2;
    /** 默认方式 */
    private static final int DEFAULT_ROUND = BigDecimal.ROUND_HALF_UP;

    /**
     * 除法
     * @param num1
     * @param num2
     * @return
     */
    public static Double divide(Number num1, Number num2){
        BigDecimal bd1 = new BigDecimal(num1 == null ? "0" : num1.toString());
        BigDecimal bd2 = new BigDecimal(num2 == null ? "0" : num2.toString());
        return bd1.divide(bd2, 10, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 转成Int型
     * @param num
     * @return
     */
    public static int paserInt(Number num){
        if(num == null) {return 0;}
        return new BigDecimal(num + "").intValue();
    }

    /**
     * 转成Long型
     * @param num
     * @return
     */
    public static long paserLong(Number num){
        if(num == null) {return 0;}
        return new BigDecimal(num + "").longValue();
    }

    /**
     * 转成String型
     * @param num
     * @return
     */
    public static Double paserDouble(String num) {
        if(num == null || "".equals(num)){
            return 0D;
        }
        return Double.valueOf(num);
    }

    /**
     * 转成Double型
     * @param amount
     * @return
     */
    public static Double parseDouble(Number amount) {
        BigDecimal bd1 = amount == null ? BigDecimal.ZERO : new BigDecimal(amount + "");
        return bd1.setScale(DEFAULT_DECIMAL, DEFAULT_ROUND).doubleValue();
    }

    /**
     * 转成BigDecimal型
     * @param num
     * @return
     */
    public static BigDecimal paserBigDecimal(Number num) {
        if (num == null) {return BigDecimal.ZERO;}
        return new BigDecimal(num + "").setScale(DEFAULT_DECIMAL, DEFAULT_ROUND);
    }

    /**
     * 分转元
     * @param amount
     * @return
     */
    public static BigDecimal fenToYuan(BigDecimal amount) {
        BigDecimal bd1 = amount == null ? BigDecimal.ZERO : amount;
        return bd1.divide(BigDecimal.valueOf(100)).setScale(DEFAULT_DECIMAL, DEFAULT_ROUND);
    }

    /**
     * 分转元
     * @param amount
     * @return
     */
    public static BigDecimal fenToYuan(Double amount) {
        BigDecimal bd1 = amount == null ? BigDecimal.ZERO : new BigDecimal(amount + "").setScale(DEFAULT_DECIMAL, DEFAULT_ROUND);
        return bd1.divide(BigDecimal.valueOf(100)).setScale(DEFAULT_DECIMAL, DEFAULT_ROUND);
    }

}
