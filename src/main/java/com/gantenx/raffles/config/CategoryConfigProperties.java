package com.gantenx.raffles.config;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import com.gantenx.raffles.config.consists.DataType;
import lombok.Data;

/**
 * 业务分类配置属性
 * 使用 Spring Boot 配置属性自动映射
 */
@Data
@ConfigurationProperties(prefix = "raffles.category")
public class CategoryConfigProperties {

    /**
     * 配置映射，key 为分类名称
     */
    private Map<String, CategoryConfigItem> configs = new HashMap<>();

    /**
     * 转换后的配置映射，供 ConfigManager 使用
     */
    private Map<Category, CategoryConfig> categoryConfigMap = new HashMap<>();

    @PostConstruct
    public void init() {
        // 将配置转换为 CategoryConfig 对象
        configs.forEach((categoryName, configItem) -> {
            try {
                Category category = Category.valueOf(categoryName.toUpperCase());
                CategoryConfig categoryConfig = convertToCategoryConfig(category, configItem);
                categoryConfigMap.put(category, categoryConfig);
            } catch (IllegalArgumentException e) {
                // 忽略未知的分类
                System.err.println("Unknown category: " + categoryName);
            }
        });
    }

    private CategoryConfig convertToCategoryConfig(Category category, CategoryConfigItem item) {
        CategoryConfig config = new CategoryConfig();
        config.setCategory(category);
        config.setEnable(item.isEnable());

        // 转换源配置
        if (item.getSource() != null) {
            config.setSourceConfig(convertToDataTypeConfig(item.getSource()));
        }

        // 转换目标配置
        if (item.getSink() != null) {
            config.setSinkConfig(convertToDataTypeConfig(item.getSink()));
        }

        return config;
    }

    private CategoryConfig.DataTypeConfig convertToDataTypeConfig(DataTypeConfigItem item) {
        CategoryConfig.DataTypeConfig config = new CategoryConfig.DataTypeConfig();

        // 设置数据类型
        DataType dataType = DataType.valueOf(item.getType().toUpperCase());
        config.setDataType(dataType);

        // 设置 Kafka 配置
        if (item.getKafka() != null) {
            CategoryConfig.Kafka kafka = new CategoryConfig.Kafka();
            kafka.setServers(item.getKafka().getServers());
            kafka.setTopic(item.getKafka().getTopic());
            config.setKafka(kafka);
        }

        // 设置 MySQL 配置
        if (item.getMysql() != null) {
            CategoryConfig.Mysql mysql = new CategoryConfig.Mysql();
            mysql.setDriver(item.getMysql().getDriver());
            mysql.setJdbcUrl(item.getMysql().getJdbcUrl());
            mysql.setUsername(item.getMysql().getUsername());
            mysql.setPassword(item.getMysql().getPassword());
            config.setMysql(mysql);
        }

        return config;
    }

    public Map<Category, CategoryConfig> getCategoryConfigMap() {
        return categoryConfigMap;
    }

    @Data
    public static class CategoryConfigItem {
        private boolean enable = true;
        private DataTypeConfigItem source;
        private DataTypeConfigItem sink;
    }

    @Data
    public static class DataTypeConfigItem {
        private String type;
        private KafkaConfigItem kafka;
        private MysqlConfigItem mysql;
    }

    @Data
    public static class KafkaConfigItem {
        private String servers;
        private String topic;
    }

    @Data
    public static class MysqlConfigItem {
        private String driver;
        private String jdbcUrl;
        private String username;
        private String password;
    }
}
