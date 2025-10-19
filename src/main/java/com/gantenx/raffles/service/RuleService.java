package com.gantenx.raffles.service;

import com.gantenx.raffles.biz.BizConfigManager;
import com.gantenx.raffles.model.RuleFlinkSql;
import com.gantenx.raffles.model.dao.CategoryDao;
import com.gantenx.raffles.model.dao.FeatureDao;
import com.gantenx.raffles.model.dao.RuleDao;
import com.gantenx.raffles.model.tables.generated.ComplianceCategory;
import com.gantenx.raffles.model.tables.generated.ComplianceFeature;
import com.gantenx.raffles.model.tables.generated.ComplianceRule;
import com.gantenx.raffles.util.GsonUtils;
import com.gantenx.raffles.util.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RuleService {
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private FeatureDao featureDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private RuleStatusService ruleStatusService;

    public boolean isDuplicateRule(RuleFlinkSql rule) {
        String expression = rule.getExecutableSql() + rule.getParams();
        String latestExpression = ruleStatusService.getLatestExpression(rule.getCode());
        Integer version = rule.getVersion();
        Integer latestVersion = ruleStatusService.getLatestVersion(rule.getCode());
        boolean expressionEqual = Objects.equals(expression, latestExpression);
        boolean versionEqual = Objects.equals(version, latestVersion);
        return expressionEqual && versionEqual;
    }

    /**
     * 用于获取所有的规则，处理:
     * 1. 数据为空
     * 2. 数据转换成业务实体
     * 3. 删除执行 sql 为空的规则
     *
     * @return 所有规则
     */
    public List<RuleFlinkSql> getRules() {
        return ruleDao.selectActiveRules().stream().filter(Objects::nonNull).map(this::toFlinkSqlRule)
                .filter(rule -> StringUtils.isNotEmpty(rule.getExecutableSql())).collect(Collectors.toList());
    }

    /**
     * rule.expression 与 feature.code 对应
     * feature.expression 为 sql 模板
     * rule.params 为 sql 模板参数
     * 通过 rule.params 与 feature.expression 生成可执行 sql
     */
    private RuleFlinkSql toFlinkSqlRule(ComplianceRule complianceRule) {
        RuleFlinkSql flinkRule = new RuleFlinkSql();
        flinkRule.setId(complianceRule.getId());
        flinkRule.setCategoryId(complianceRule.getCategoryId());
        flinkRule.setCode(complianceRule.getCode());
        flinkRule.setVersion(complianceRule.getVersion());
        flinkRule.setParams(complianceRule.getParams());
        flinkRule.setParamsDesc(complianceRule.getParamsDesc());
        ComplianceCategory category = categoryDao.selectByCategoryId(flinkRule.getCategoryId());
        if (category != null) {
            flinkRule.setCategoryCode(category.getCode());
            flinkRule.setBizType(BizConfigManager.getBizType(category.getCode()));
        }
        String expression = complianceRule.getExpression();
        ComplianceFeature feature;
        if (StringUtils.isEmpty(expression) || Objects.isNull(feature = featureDao.selectByFeatureCode(expression))) {
            log.error("rule without expression, ruleCode:{}", complianceRule.getCode());
            return flinkRule;
        }

        String params = flinkRule.getParams();
        Map<String, Object> paramMap = GsonUtils.toMap(params);
        String executableSql = SqlUtils.getExecutableSql(feature.getExpression(), paramMap);
        flinkRule.setExecutableSql(executableSql);
        return flinkRule;
    }
}
