package com.liuyun.github.utils;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author: lewis
 * @create: 2020/4/20 上午10:39
 */
@Slf4j
public abstract class PressureTest {

    public static Map<String, Long> periodMap = Maps.newConcurrentMap();

    public int threadNum;

    public int loopNum;

    public PressureTest () {
        this.threadNum = 1;
        this.loopNum = 5;
    }

    public PressureTest (Integer threadNum, Integer loopNum) {
        this.threadNum = threadNum;
        this.loopNum = loopNum;
    }

    public void execute() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        List<Thread> threadList = new ArrayList();
        for (int i = 0; i < threadNum; i++) {
            threadList.add(new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < loopNum; i++) {
                        String key = String.format("[%s][%s]", Thread.currentThread().getName(), i);
                        try {
                            long period = doExecute();
                            periodMap.put(key, period);
                        } catch (Exception e) {
                            log.error(String.format("%s调用出错", key), e);
                        }
                    }
                    countDownLatch.countDown();
                }
            });
        }
        long start = System.currentTimeMillis();
        for (Thread thread : threadList) {
            thread.start();
        }
        countDownLatch.await();
        long end = System.currentTimeMillis();
        Report report = buildReport(periodMap, start, end);
        System.out.print(String.format("总的请求数: %s", report.getTotalRequestNum()));
        System.out.print(String.format("总的响应数: %s", report.getTotalResponseNum()));
        System.out.print(String.format("平均响应时间: %s", report.getAvgResponseTime()));
        System.out.print(String.format("错误率: %s", report.getErrorRatio()));
    }

    /**
     * 构建压测报告
     * @param periodMap
     * @return
     */
    public Report buildReport(Map<String, Long> periodMap, Long start, Long end) {
        //总的请求数量
        BigDecimal totalRequestNum = BigDecimal.valueOf(threadNum).multiply(BigDecimal.valueOf(loopNum));
        //总的响应数量
        BigDecimal totalResponseNum = BigDecimal.valueOf(periodMap.keySet().size());
        //总的出错数量
        BigDecimal totalErrorNum = totalRequestNum.subtract(totalResponseNum);
        //总的响应时间
        BigDecimal totalResponseTime = BigDecimal.valueOf(periodMap.values().stream().mapToLong(Long::longValue).sum());
        //平均响应时间
        BigDecimal avgResponseTime = totalResponseTime.divide(totalResponseNum, 2, RoundingMode.HALF_DOWN);
        //错误率
        BigDecimal errorRatio = totalErrorNum.divide(totalRequestNum, 2, RoundingMode.HALF_DOWN);
        //总耗时(单位秒)
        BigDecimal totalCostTime = BigDecimal.valueOf(end - start).divide(BigDecimal.valueOf(1000), 0, RoundingMode.HALF_DOWN);
        //QPS
        BigDecimal qps = totalResponseNum.divide(totalCostTime, 0, RoundingMode.HALF_DOWN);
        Report report = new Report();
        report.setTotalRequestNum(totalRequestNum.intValue());
        report.setTotalResponseNum(totalResponseNum.intValue());
        report.setAvgResponseTime(avgResponseTime.doubleValue());
        report.setErrorRatio(errorRatio.doubleValue());
        report.setQps(qps.intValue());
        return report;
    }

    protected abstract long doExecute();

    @Data
    public static class Report {

        /** 总的请求数 */
        private int totalRequestNum;
        /** 总的响应数 */
        private int totalResponseNum;
        /** 平均响应时间 */
        private double avgResponseTime;
        /** 错误率 */
        private double errorRatio;
        /** QPS */
        private int qps;

    }


}
