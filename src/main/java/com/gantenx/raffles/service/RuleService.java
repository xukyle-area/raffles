package com.gantenx.raffles.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gantenx.raffles.config.Category;
import com.gantenx.raffles.config.ConfigManager;
import com.gantenx.raffles.model.FlinkRule;
import com.gantenx.raffles.model.dao.RuleDao;
import com.gantenx.raffles.model.dao.SqlTemplateDao;
import com.gantenx.raffles.model.entity.Rule;
import com.gantenx.raffles.model.entity.SqlTemplate;
import com.gantenx.raffles.utils.GsonUtils;
import com.gantenx.raffles.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于构建 FlinkRule 对象
 */
@Slf4j
@Service
public class RuleService {

    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private SqlTemplateDao sqlTemplateDao;
    @Autowired
    private RuleStatusCache ruleStatusService;

    /**
     * 判断规则是否为重复规则（未发生变更）
     * @param rule  要检查的规则
     * @return true 如果规则未变更，false 如果规则已变更
     */
    public boolean isDuplicateRule(FlinkRule rule) {
        String expression = rule.getExecutableSql() + rule.getParams();
        String latestExpression = ruleStatusService.getLatestExpression(rule.getName());
        Integer version = rule.getVersion();
        Integer latestVersion = ruleStatusService.getLatestVersion(rule.getName());
        boolean expressionEqual = Objects.equals(expression, latestExpression);
        boolean versionEqual = Objects.equals(version, latestVersion);
        return expressionEqual && versionEqual;
    }

    /**
     * 获取所有的规则列表
     * @return 规则列表
     */
    public List<FlinkRule> getRules() {
        return ruleDao.selectActiveRules().stream().filter(Objects::nonNull).map(this::toFlinkRule)
                .filter(rule -> StringUtils.isNotEmpty(rule.getExecutableSql())).collect(Collectors.toList());
    }

    /**
     * 获取指定类别的规则列表
     * 用于 Flink 任务提交
     * @param category 规则类别
     * @return 规则列表
     */
    public List<FlinkRule> getRules(Category category) {
        return this.getRules().stream().filter(o -> category.equals(o.getCategory())).collect(Collectors.toList());
    }

    /**
     * rule.expression 与 feature.code 对应
     * feature.expression 为 sql 模板
     * rule.params 为 sql 模板参数
     * 通过 rule.params 与 feature.expression 生成可执行 sql
     */
    private FlinkRule toFlinkRule(Rule rule) {
        FlinkRule flinkRule = new FlinkRule();
        flinkRule.setId(rule.getId());
        flinkRule.setCategoryId(rule.getCategoryId());
        flinkRule.setName(rule.getCode());
        flinkRule.setVersion(rule.getVersion());
        flinkRule.setParams(rule.getParams());
        flinkRule.setParamsDesc(rule.getParamsDesc());
        Category category = Category.getCategory(rule.getCategoryId());
        flinkRule.setCategory(category);
        if (category != null) {
            flinkRule.setCategoryName(category.getName());
            flinkRule.setCategoryConfig(ConfigManager.getCategoryConfig(category));
        }
        int sqlTemplateId = rule.getSqlTemplateId();
        SqlTemplate feature = sqlTemplateDao.selectById(sqlTemplateId);
        if (Objects.isNull(feature)) {
            log.error("rule without expression, ruleCode:{}", rule.getCode());
            return flinkRule;
        }
        String params = flinkRule.getParams();
        Map<String, Object> paramMap = GsonUtils.toMap(params);
        String executableSql = SqlUtils.getExecutableSql(feature.getExpression(), paramMap);
        flinkRule.setExecutableSql(executableSql);
        return flinkRule;
    }
}
