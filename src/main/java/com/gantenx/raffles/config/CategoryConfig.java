package com.gantenx.raffles.config;

import java.io.Serializable;
import com.gantenx.raffles.config.consists.DataType;
import lombok.Data;

@Data
public class CategoryConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Category category;
    /**
     * 业务是否上线
     */
    private boolean enable;
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
    public static class DataTypeConfig implements Serializable {
        private static final long serialVersionUID = 1L;

        private DataType dataType;
        private Mysql mysql;
        private Kafka kafka;
    }

    @Data
    public static class Mysql implements Serializable {

        private static final long serialVersionUID = 1L;

        private String driver;
        private String jdbcUrl;
        private String username;
        private String password;

    }

    @Data
    public static class Kafka implements Serializable {
        private static final long serialVersionUID = 1L;

        private String servers;
        private String topic;
    }
}
