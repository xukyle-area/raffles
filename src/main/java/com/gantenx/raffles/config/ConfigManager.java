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

    private final CategoryConfigProperties instanceConfigProperties;

    public ConfigManager(CategoryConfigProperties categoryConfigProperties) {
        this.instanceConfigProperties = categoryConfigProperties;
    }

    // 实例方法，直接使用注入的配置
    public CategoryConfig getCategoryConfig(Category category) {
        Map<Category, CategoryConfig> configMap = instanceConfigProperties.getCategoryConfigMap();
        if (configMap == null) {
            return null;
        }
        return configMap.get(category);
    }

    public List<CategoryConfig> getCategoryConfigList() {
        Map<Category, CategoryConfig> configMap = instanceConfigProperties.getCategoryConfigMap();
        return new ArrayList<>(configMap.values());
    }
}
