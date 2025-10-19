package com.gantenx.raffles.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private static final Map<Category, CategoryConfig> categoryConfigMap = new HashMap<>();

    static {
        // 初始化各类别的配置
        CategoryConfig calculateConfig = new CategoryConfig();
        // 这里可以添加更多的初始化配置
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
