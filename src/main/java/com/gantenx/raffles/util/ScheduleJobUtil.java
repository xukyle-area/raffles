package com.gantenx.raffles.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScheduleJobUtil {
    /**
     * 计算每天的第n个小时执行的时间
     *
     * @param n 每天的第n个小时
     * @return 初始延迟时间
     */
    public static long calculateInitialDelay(int n) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(n).withMinute(0).withSecond(0);
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }
        return ChronoUnit.MILLIS.between(now, nextRun);
    }

    /**
     * 代理任务
     *
     * @param task     任务
     * @param taskName 任务名称
     * @return Runnable
     */
    public static Runnable wrapWithLogging(Runnable task, String taskName) {
        return () -> {
            try {
                long begin = System.currentTimeMillis();
                log.info("Start task {}", taskName);
                task.run();
                log.info("End task {}, cost {}ms", taskName, System.currentTimeMillis() - begin);
            } catch (Exception e) {
                log.error("Error in {}", taskName, e);
            }
        };
    }
}
