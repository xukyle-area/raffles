package com.gantenx.raffles.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScheduledThreadPool {
    // 私有静态变量，用于保存定时调度线程池实例
    private static final ScheduledExecutorService scheduledExecutor;
    private static final int POOL_SIZE = 5;
    private static final long ZERO_INITIAL_DELAY = 5L;

    static {
        scheduledExecutor = Executors.newScheduledThreadPool(POOL_SIZE, r -> {
            Thread thread = new Thread(r);
            thread.setName("flink-job-" + thread.getId());
            return thread;
        });
    }

    // 私有构造函数，防止外部实例化
    private ScheduledThreadPool() {}

    public static void scheduleWithFixedDelay(Runnable task, long fixedDelay, long initialDelay, String taskName) {
        scheduledExecutor.scheduleWithFixedDelay(ScheduleJobUtil.wrapWithLogging(task, taskName), initialDelay,
                fixedDelay, TimeUnit.SECONDS);
    }


    /**
     * 调度任务
     *
     * @param task       任务
     * @param fixedDelay 固定延迟
     * @param taskName   任务名称
     */
    public static void scheduleWithFixedDelay(Runnable task, long fixedDelay, String taskName) {
        scheduledExecutor.scheduleWithFixedDelay(ScheduleJobUtil.wrapWithLogging(task, taskName), ZERO_INITIAL_DELAY,
                fixedDelay, TimeUnit.SECONDS);
    }

    /**
     * 调度任务
     *
     * @param task         任务
     * @param initialDelay 初始延迟
     * @param period       周期
     * @param taskName     任务名称
     */
    public static void scheduleAtFixedRate(Runnable task, long initialDelay, long period, String taskName) {
        scheduledExecutor.scheduleAtFixedRate(ScheduleJobUtil.wrapWithLogging(task, taskName), initialDelay, period,
                TimeUnit.SECONDS);
    }

    // 关闭定时调度线程池
    public static void shutdown() {
        scheduledExecutor.shutdown();
    }
}
