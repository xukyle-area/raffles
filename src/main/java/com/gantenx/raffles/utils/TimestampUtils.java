package com.gantenx.raffles.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间戳工具类
 */
public class TimestampUtils {
    private static final ZoneId SHANGHAI_ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 将时间戳转换为上海时区（UTC+8）的格式化字符串
     * @param timestamp 时间戳
     * @param <T> 数字类型（Long, Integer, Double等）
     * @return 格式化的时间字符串
     */
    public static <T extends Number> String formatTimestamp(T timestamp) {
        return formatTimestamp(timestamp, DEFAULT_FORMATTER);
    }

    /**
     * 将时间戳转换为上海时区（UTC+8）的格式化字符串，使用指定的格式
     * @param timestamp 时间戳
     * @param formatter 时间格式
     * @param <T> 数字类型（Long, Integer, Double等）
     * @return 格式化的时间字符串
     */
    public static <T extends Number> String formatTimestamp(T timestamp, DateTimeFormatter formatter) {
        long millis = timestamp.longValue();
        Instant instant = Instant.ofEpochMilli(millis);
        ZonedDateTime zonedDateTime = instant.atZone(SHANGHAI_ZONE);
        return zonedDateTime.format(formatter);
    }

    /**
     * 将时间戳转换为指定时区的格式化字符串
     * @param timestamp 时间戳
     * @param zoneId 时区
     * @param formatter 时间格式
     * @param <T> 数字类型（Long, Integer, Double等）
     * @return 格式化的时间字符串
     */
    public static <T extends Number> String formatTimestamp(T timestamp, ZoneId zoneId, DateTimeFormatter formatter) {
        long millis = timestamp.longValue();
        Instant instant = Instant.ofEpochMilli(millis);
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);
        return zonedDateTime.format(formatter);
    }

    public static final long MILLIS_OF_ONE_DAY = 24 * 60 * 60 * 1000;
    public static final long MILLIS_OF_ONE_HOUR = 60 * 60 * 1000;
    public static final int DAY_SEC = 60 * 60 * 24;

    /**
     * 获取今天 UTC 0 点的时间戳
     * 例如, 现在是 2025-10-03 13:57:23 UTC，那么 midnightTimestampToday() 返回 2025-10-03 00:00:00 UTC 的时间戳
     */
    public static long midnightTimestampToday() {
        long totalDay = System.currentTimeMillis() / MILLIS_OF_ONE_DAY;
        // 计算从 1970-01-01 到今天的总天数，然后乘以一天的毫秒数
        return totalDay * MILLIS_OF_ONE_DAY;
    }

    /**
     * 返回 i 天前的 UTC 0 点时间戳
     * 例如, 现在是 2025-10-03 13:57:23 UTC，那么:
     * 1. midnightTimestampBefore(0) 返回 2025-10-03 00:00:00 UTC 的时间戳
     * 2. midnightTimestampBefore(1) 返回 2025-10-02 00:00:00 UTC 的时间戳
     */
    public static long midnightTimestampBefore(int i) {
        return midnightTimestampToday() - i * MILLIS_OF_ONE_DAY;
    }

    /**
     * 计算距离下一个 UTC n 点的毫秒数
     * 例如，如果现在是 08:50:00 UTC，那么:
     * 1. millisecondsUntilUTCHour(10) 返回 70 分钟的毫秒数, 因为再过 70 分钟就是今天的 10 点
     * 2. millisecondsUntilUTCHour(7) 返回 22 小时 10 分钟的毫秒数, 因为再过 22 小时 10 分钟就是明天的 7 点，今天的 7 点已经过去了
     */
    public static long millisecondsUntilUTCHour(int n) {
        long todayUTC_N = midnightTimestampToday() + MILLIS_OF_ONE_HOUR * n;
        long leftMilliseconds = todayUTC_N - System.currentTimeMillis();
        return leftMilliseconds > 0 ? leftMilliseconds : leftMilliseconds + MILLIS_OF_ONE_DAY;
    }
}
