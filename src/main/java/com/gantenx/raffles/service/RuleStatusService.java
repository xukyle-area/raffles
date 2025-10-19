package com.gantenx.raffles.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Service
public class RuleStatusService {
    private static final String RULE_TO_EXPRESSION = "rule_expression:";
    private static final String RULE_TO_VERSION = "rule_version:";

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public void setLatestExpression(String ruleCode, String sql) {
        String key = RULE_TO_EXPRESSION + ruleCode;
        try {

            BoundValueOperations<String, String> valueOps = redisTemplate.boundValueOps(key);
            valueOps.set(sql);
        } catch (Exception e) {
            log.error("setLatestExpression, key: {}", key, e);
        }
    }

    public String getLatestExpression(String ruleCode) {
        String key = RULE_TO_EXPRESSION + ruleCode;
        try {

            BoundValueOperations<String, String> valueOps = redisTemplate.boundValueOps(key);
            Object expression = valueOps.get();
            if (expression != null) {
                return expression.toString();
            }
        } catch (Exception e) {
            log.error("getLatestExpression, key: {}", key, e);
        }

        return null;
    }

    public Integer getLatestVersion(String ruleCode) {
        String key = RULE_TO_VERSION + ruleCode;
        try {

            BoundValueOperations<String, String> valueOps = redisTemplate.boundValueOps(key);
            Object version = valueOps.get();
            if (Objects.nonNull(version)) {
                return Integer.valueOf(version.toString());
            }
        } catch (Exception e) {
            log.error("getLatestVersion, key: {}", key, e);
        }

        return null;
    }

    public void setLatestVersion(String ruleCode, Integer version) {
        String key = RULE_TO_VERSION + ruleCode;
        try {
            BoundValueOperations<String, String> valueOps = redisTemplate.boundValueOps(key);
            valueOps.set(String.valueOf(version));
        } catch (Exception e) {
            log.error("setLatestVersion, key: {}", key, e);
        }
    }
}
