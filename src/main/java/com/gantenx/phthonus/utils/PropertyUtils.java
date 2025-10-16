package com.gantenx.phthonus.utils;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class PropertyUtils {

    /**
     * 设置日志级别
     */
    public static void initLogLevel() {
        // 设置日志级别
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");
        System.setProperty("org.slf4j.simpleLogger.log.org.apache.kafka", "INFO");
        System.setProperty("org.slf4j.simpleLogger.log.kafka", "INFO");
        System.setProperty("org.slf4j.simpleLogger.log.org.apache.kafka.clients", "INFO");
        System.setProperty("org.slf4j.simpleLogger.log.org.apache.kafka.common", "INFO");

        // 如果使用的是 Logback
        System.setProperty("logging.level.org.apache.kafka", "INFO");
        System.setProperty("logging.level.kafka", "INFO");

        // 如果使用的是 Log4j
        System.setProperty("log4j.logger.org.apache.kafka", "INFO");
        System.setProperty("log4j.logger.kafka", "INFO");

        // 对于Logback
        Logger kafkaLogger = (Logger) LoggerFactory.getLogger("org.apache.kafka");
        kafkaLogger.setLevel(Level.INFO);

        Logger kafkaClientsLogger = (Logger) LoggerFactory.getLogger("org.apache.kafka.clients");
        kafkaClientsLogger.setLevel(Level.INFO);
    }
}
