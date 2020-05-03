package com.liuyun.github.utils;

import java.io.Serializable;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

public class QpsUtils {

    private RollingNumber rollingNumber;

    public QpsUtils() {
        this.rollingNumber = new RollingNumber();
    }


    public void pass() {
        rollingNumber.record();
    }


    private static final class RollingNumber {

        /** 槽位的数量 */
        private int sizeOfBuckets;

        /** 时间片，单位毫秒 */
        private int unitOfTimeSlice;

        /** 用于判断是否可跳过锁争抢 */
        private int timeSliceUsedToCheckIfPossibleToBypass;

        /** 槽位 */
        private Bucket[] buckets;

        /** 目标槽位的位置 */
        private volatile Integer targetBucketPosition;

        /** 接近目标槽位最新更新时间的时间 */
        private volatile Long latestPassedTimeCloseToTargetBucket;

        /** 进入下一个槽位时使用的锁 */
        private ReentrantLock enterNextBucketLock;

        /** 默认60个槽位，槽位的时间片为1000毫秒 */
        public RollingNumber() {
            this(60, 1000);
        }


        public RollingNumber(int sizeOfBuckets, int unitOfTimeSlice) {
            this.latestPassedTimeCloseToTargetBucket = System.currentTimeMillis() - (2 * unitOfTimeSlice);
            this.targetBucketPosition = null;
            this.sizeOfBuckets = sizeOfBuckets;
            this.unitOfTimeSlice = unitOfTimeSlice;
            this.enterNextBucketLock = new ReentrantLock();
            this.buckets = new Bucket[sizeOfBuckets];
            this.timeSliceUsedToCheckIfPossibleToBypass = 3 * unitOfTimeSlice;
            for (int i = 0; i < sizeOfBuckets; i++) {
                this.buckets[i] = new Bucket();
            }
        }


        private void record() {
            //获取当前时间
            long passTime = System.currentTimeMillis();
            //如果目标槽位位空，则重新计算目标槽位
            if (targetBucketPosition == null) {
                targetBucketPosition = (int) (passTime / unitOfTimeSlice) % sizeOfBuckets;
            }
            //获取当前槽位
            Bucket currentBucket = buckets[targetBucketPosition];
            //如果时间间隔大于时间片
            if (passTime - latestPassedTimeCloseToTargetBucket >= unitOfTimeSlice) {
                //如果已经锁了，并且时间间隔小于
                if (enterNextBucketLock.isLocked() && (passTime - latestPassedTimeCloseToTargetBucket) < timeSliceUsedToCheckIfPossibleToBypass) {

                } else {
                    try {
                        //加锁
                        enterNextBucketLock.lock();
                        //再判断一次
                        if (passTime - latestPassedTimeCloseToTargetBucket >= unitOfTimeSlice) {
                            //计算槽位
                            int nextTargetBucketPosition = (int) (passTime / unitOfTimeSlice) % sizeOfBuckets;
                            //获取下一个槽位
                            Bucket nextBucket = buckets[nextTargetBucketPosition];
                            if (nextBucket.equals(currentBucket)) {
                                if (passTime - latestPassedTimeCloseToTargetBucket >= unitOfTimeSlice) {
                                    latestPassedTimeCloseToTargetBucket = passTime;
                                }
                            } else {
                                //重置槽位
                                nextBucket.reset(passTime);
                                //更新目标位置
                                targetBucketPosition = nextTargetBucketPosition;
                                latestPassedTimeCloseToTargetBucket = passTime;
                            }
                            //统计值加1
                            nextBucket.pass();
                            return;
                        } else {
                            currentBucket = buckets[targetBucketPosition];
                        }
                    } finally {
                        enterNextBucketLock.unlock();
                    }
                }
            }
            //如果不大于时间片，则直接加1
            currentBucket.pass();
        }

        public Bucket[] getBuckets() {
            return buckets;
        }

    }


    private static class Bucket implements Serializable {

        private static final long serialVersionUID = -9085720164508215774L;

        private Long latestPassedTime;

        private LongAdder longAdder;

        public Bucket() {
            this.latestPassedTime = System.currentTimeMillis();
            this.longAdder = new LongAdder();
        }


        public void pass() {
            longAdder.add(1);
        }

        public long countTotalPassed() {
            return longAdder.sum();
        }

        public long getLatestPassedTime() {
            return latestPassedTime;
        }

        public void reset(long latestPassedTime) {
            this.longAdder.reset();
            this.latestPassedTime = latestPassedTime;
        }

    }


}
