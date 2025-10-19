package com.gantenx.raffles.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;
import com.gantenx.raffles.config.consists.DataType;

public class ConfigManager {

    private static final Map<Category, CategoryConfig> categoryConfigMap = new HashMap<>();

    static {
        loadConfigFromYaml();
    }

    @SuppressWarnings("unchecked")
    private static void loadConfigFromYaml() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = ConfigManager.class.getClassLoader().getResourceAsStream("application.yaml");

            if (inputStream != null) {
                Map<String, Object> data = yaml.load(inputStream);
                Map<String, Object> raffles = (Map<String, Object>) data.get("raffles");

                if (raffles != null) {
                    Map<String, Object> category = (Map<String, Object>) raffles.get("category");
                    if (category != null) {
                        Map<String, Object> configs = (Map<String, Object>) category.get("configs");
                        if (configs != null) {
                            // 解析 CALCULATE 配置
                            Map<String, Object> calculateConfig = (Map<String, Object>) configs.get("calculate");
                            if (calculateConfig != null) {
                                CategoryConfig config = buildCategoryConfig(Category.CALCULATE, calculateConfig);
                                categoryConfigMap.put(Category.CALCULATE, config);
                            }
                        }
                    }
                }
                inputStream.close();
            }
        } catch (Exception e) {
            // 如果读取 YAML 失败，使用默认配置
            initDefaultConfig();
        }
    }

    @SuppressWarnings("unchecked")
    private static CategoryConfig buildCategoryConfig(Category category, Map<String, Object> configData) {
        CategoryConfig config = new CategoryConfig();
        config.setCategory(category);
        config.setEnable((Boolean) configData.getOrDefault("enable", true));
        config.setBatch((Boolean) configData.getOrDefault("batch", false));

        // 构建源配置
        Map<String, Object> sourceData = (Map<String, Object>) configData.get("source");
        if (sourceData != null) {
            config.setSourceConfig(buildDataTypeConfig(sourceData));
        }

        // 构建目标配置
        Map<String, Object> sinkData = (Map<String, Object>) configData.get("sink");
        if (sinkData != null) {
            config.setSinkConfig(buildDataTypeConfig(sinkData));
        }

        return config;
    }

    @SuppressWarnings("unchecked")
    private static CategoryConfig.DataTypeConfig buildDataTypeConfig(Map<String, Object> data) {
        CategoryConfig.DataTypeConfig config = new CategoryConfig.DataTypeConfig();

        String type = (String) data.get("type");
        if ("kafka".equalsIgnoreCase(type)) {
            config.setDataType(DataType.KAFKA);
            Map<String, Object> kafkaData = (Map<String, Object>) data.get("kafka");
            if (kafkaData != null) {
                CategoryConfig.Kafka kafka = new CategoryConfig.Kafka();
                kafka.setServers((String) kafkaData.get("servers"));
                kafka.setTopic((String) kafkaData.get("topic"));
                config.setKafka(kafka);
            }
        } else if ("mysql".equalsIgnoreCase(type)) {
            config.setDataType(DataType.MYSQL);
            Map<String, Object> mysqlData = (Map<String, Object>) data.get("mysql");
            if (mysqlData != null) {
                CategoryConfig.Mysql mysql = new CategoryConfig.Mysql();
                mysql.setDriver((String) mysqlData.get("driver"));
                mysql.setJdbcUrl((String) mysqlData.get("jdbc-url"));
                mysql.setUsername((String) mysqlData.get("username"));
                mysql.setPassword((String) mysqlData.get("password"));
                config.setMysql(mysql);
            }
        }

        return config;
    }

    private static void initDefaultConfig() {
        // 如果 YAML 读取失败，使用默认配置
        CategoryConfig calculateConfig = new CategoryConfig();
        calculateConfig.setCategory(Category.CALCULATE);
        calculateConfig.setEnable(true);
        calculateConfig.setBatch(false);
        categoryConfigMap.put(Category.CALCULATE, calculateConfig);
    }

    public static CategoryConfig getCategoryConfig(int categoryId) {
        Category category = Category.getCategory(categoryId);
        return categoryConfigMap.get(category);
    }

    public static CategoryConfig getCategoryConfig(Category category) {
        return categoryConfigMap.get(category);
    }

    public static List<CategoryConfig> getCategoryConfigList() {
        return new ArrayList<>(categoryConfigMap.values());
    }
}
