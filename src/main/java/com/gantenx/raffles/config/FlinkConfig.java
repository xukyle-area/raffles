package com.gantenx.raffles.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

/**
 * Flink 配置类
 * 从 application.yaml 中读取 flink 相关配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "flink")
public class FlinkConfig {
    private String savepointPath;
    private String host;
    private int port;
}
