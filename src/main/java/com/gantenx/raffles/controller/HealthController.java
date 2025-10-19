package com.gantenx.raffles.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gantenx.raffles.config.Category;
import com.gantenx.raffles.config.CategoryConfig;
import com.gantenx.raffles.config.ConfigManager;

/**
 * 健康检查控制器
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("timestamp", System.currentTimeMillis());

        // 检查配置是否正常加载
        try {
            CategoryConfig config = ConfigManager.getCategoryConfig(Category.CALCULATE);
            if (config != null) {
                result.put("config", "LOADED");
                result.put("configEnabled", config.isEnable());
            } else {
                result.put("config", "NOT_FOUND");
            }
        } catch (Exception e) {
            result.put("config", "ERROR: " + e.getMessage());
        }

        return result;
    }
}
