package com.gantenx.raffles.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import com.gantenx.raffles.model.FlinkRule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RuleStatusCache {
    private static final String RULE_TO_EXPRESSION = "rule_expression:";
    private static final String RULE_TO_VERSION = "rule_version:";

    // 使用内存存储替代Redis
    private final Map<String, String> expressionCache = new ConcurrentHashMap<>();
    private final Map<String, Integer> versionCache = new ConcurrentHashMap<>();

    public void setLatestExpression(String ruleCode, String sql) {
        String key = RULE_TO_EXPRESSION + ruleCode;
        try {
            expressionCache.put(key, sql);
            log.info("setLatestExpression, key: {}, sql: {}", key, sql);
        } catch (Exception e) {
            log.error("setLatestExpression, key: {}", key, e);
        }
    }

    public String getLatestExpression(String ruleCode) {
        String key = RULE_TO_EXPRESSION + ruleCode;
        try {
            String expression = expressionCache.get(key);
            log.info("getLatestExpression, key: {}, expression: {}", key, expression);
            return expression;
        } catch (Exception e) {
            log.error("getLatestExpression, key: {}", key, e);
        }

        return null;
    }

    public Integer getLatestVersion(String ruleCode) {
        String key = RULE_TO_VERSION + ruleCode;
        try {
            Integer version = versionCache.get(key);
            log.info("getLatestVersion, key: {}, version: {}", key, version);
            return version;
        } catch (Exception e) {
            log.error("getLatestVersion, key: {}", key, e);
        }

        return null;
    }

    public void setLatestVersion(String ruleCode, Integer version) {
        String key = RULE_TO_VERSION + ruleCode;
        try {
            versionCache.put(key, version);
            log.info("setLatestVersion, key: {}, version: {}", key, version);
        } catch (Exception e) {
            log.error("setLatestVersion, key: {}", key, e);
        }
    }


    /**
     * 更新规则状态缓存
     * 缓存 version 和 expression，用于检测规则变更
     */
    public void updateRuleState(FlinkRule rule) {
        String ruleName = rule.getName();
        String expression = buildExpression(rule);

        this.setLatestExpression(ruleName, expression);
        this.setLatestVersion(ruleName, rule.getVersion());

        log.debug("Updated rule state cache for: {}", ruleName);
    }

    /**
     * 获取规则的完整表达式（SQL + 参数）
     */
    private String buildExpression(FlinkRule rule) {
        return rule.getExecutableSql() + rule.getParams();
    }

    /**
     * 清除指定规则的状态缓存
     */
    public void clearRuleState(String ruleName) {
        this.setLatestExpression(ruleName, null);
        this.setLatestVersion(ruleName, null);
        log.debug("Cleared rule state cache for: {}", ruleName);
    }
}
