package com.liuyun.github.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liuyun18
 * @Date: 2019/1/24 下午7:45
 */
public class ThreadPoolRepo {

    private static ExecutorService executorService;

    public static ExecutorService getThreadPool() {
        if(executorService == null) {
            executorService = new ThreadPoolExecutor(5, 50, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(1024));
        }
        return executorService;
    }

}
