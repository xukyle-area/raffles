package com.gantenx.raffles.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 配置管理器
 * 使用 Spring 依赖注入获取配置
 */
@Component
public class ConfigManager {

    private static CategoryConfigProperties categoryConfigProperties;

    public ConfigManager(CategoryConfigProperties categoryConfigProperties) {
        ConfigManager.categoryConfigProperties = categoryConfigProperties;
    }

    public static CategoryConfig getCategoryConfig(int categoryId) {
        Category category = Category.getCategory(categoryId);
        return getCategoryConfig(category);
    }

    public static CategoryConfig getCategoryConfig(Category category) {
        Map<Category, CategoryConfig> configMap = categoryConfigProperties.getCategoryConfigMap();
        return configMap.get(category);
    }

    public static List<CategoryConfig> getCategoryConfigList() {
        Map<Category, CategoryConfig> configMap = categoryConfigProperties.getCategoryConfigMap();
        return new ArrayList<>(configMap.values());
    }
}
