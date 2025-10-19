package com.gantenx.raffles.config;

import com.gantenx.raffles.config.consists.DataType;
import lombok.Data;

@Data
public class CategoryConfig {
    /**
     * 业务类型 与 BizType
     *
     * @see BizType#getCode()
     */
    private Category category;
    /**
     * 业务是否上线
     */
    private boolean enable;
    /**
     * 业务是否是批处理
     */
    private boolean isBatch;
    /**
     * 业务 flink 的来源配置
     */
    private DataTypeConfig sourceConfig;
    /**
     * 业务 flink 的 sink 配置
     */
    private DataTypeConfig sinkConfig;

    /**
     * Source 与 Sink 的配置
     * mysql 与 kafka 的配置其中一个为空
     * 为了兼容两种类型的数据类型的配置
     */
    @Data
    public static class DataTypeConfig {
        private DataType dataType;
        private Mysql mysql;
        private Kafka kafka;
    }

    @Data
    public static class Mysql {
        private String driver;
        private String jdbcUrl;
        private String username;
        private String password;

    }

    @Data
    public static class Kafka {
        private String servers;
        private String topic;
    }
}
