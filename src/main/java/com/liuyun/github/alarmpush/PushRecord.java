package com.liuyun.github.alarmpush;

import lombok.Data;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: liuyun18
 * @Date: 2019/2/20 下午6:52
 */
@Data
public class PushRecord {

    /** 上次推送时间 */
    private long lastPushTime;
    /** 推送周期(毫秒) */
    private long period;
    /** 推送阀值 */
    private int threshold;
    /** 推送次数 */
    private AtomicInteger times;

    /**
     * 构造器
     */
    public PushRecord(long period, int threshold) {
        this.lastPushTime = System.currentTimeMillis();
        this.period = period;
        this.threshold = threshold;
        this.times = new AtomicInteger(0);
    }

    /**
     * 获取推送次数
     * @return
     */
    public int getTimes() {
        return times.get();
    }

    /**
     * 增加推送次数
     * @return
     */
    public int incrementTimes() {
        return times.getAndIncrement();
    }

    /**
     * 是否限制推送
     * @return
     */
    public boolean isAllow() {
        return getTimes() < this.threshold;
    }

}
