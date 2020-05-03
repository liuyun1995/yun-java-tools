package com.liuyun.github.utils;

/**
 * @Author: liuyun18
 * @Date: 2018/11/28 下午2:47
 */
public class BitUtils {

    /**
     * 获取某位的值
     * @param num
     * @param index
     * @return
     */
    public static boolean getBit(int num, int index) {
        return ((num & (1 << index)) != 0);
    }

    /**
     * 整数中1的个数
     * @param num
     * @return
     */
    public static int numberOfOne(int num) {
        int count = 0;
        while (num != 0) {
            count++;
            num = (num - 1) & num;
        }
        return count;
    }

    /**
     * 整数中0的个数
     * @param num
     * @return
     */
    public static int numberOfZero(int num) {
        int count = 0;
        while(num != 0) {
            count++;
            num = num | (num + 1);
        }
        return count;
    }

    /**
     * 取反运算
     */
    public static int inverse(int num, int... indexs) {
        for (int index : indexs) {
            num = getBit(num, index) ? setZero(num, index) : setOne(num, index);
        }
        return num;
    }

    /**
     * 将某些位设置为1
     * @param num
     * @param indexs
     * @return
     */
    public static int setOne(int num, int... indexs) {
        for (int index : indexs) {
            num = num | (1 << index);
        }
        return num;
    }

    /**
     * 将某些位设置为0
     * @param num
     * @param indexs
     * @return
     */
    public static int setZero(int num, int... indexs) {
        for (int index : indexs) {
            num = num & ~(1 << index);
        }
        return num;
    }

    public static void main(String[] args) {
        int i = 0;
        i = setOne(i, 1, 2, 3, 4);
        int n = numberOfOne(i);
        i = inverse(i, 2);
        n = numberOfOne(i);
        boolean x = getBit(i, 3);
        System.out.println(i);
    }

}
