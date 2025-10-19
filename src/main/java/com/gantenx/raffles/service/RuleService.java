package com.gantenx.raffles.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gantenx.raffles.biz.BizConfigManager;
import com.gantenx.raffles.config.Category;
import com.gantenx.raffles.model.RuleFlinkSql;
import com.gantenx.raffles.model.dao.RuleDao;
import com.gantenx.raffles.model.dao.SqlTemplateDao;
import com.gantenx.raffles.model.entity.Rule;
import com.gantenx.raffles.model.entity.SqlTemplate;
import com.gantenx.raffles.utils.GsonUtils;
import com.gantenx.raffles.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RuleService {
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private SqlTemplateDao sqlTemplateDao;

    @Autowired
    private RuleStatusService ruleStatusService;

    public boolean isDuplicateRule(RuleFlinkSql rule) {
        String expression = rule.getExecutableSql() + rule.getParams();
        String latestExpression = ruleStatusService.getLatestExpression(rule.getName());
        Integer version = rule.getVersion();
        Integer latestVersion = ruleStatusService.getLatestVersion(rule.getName());
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
    private RuleFlinkSql toFlinkSqlRule(Rule complianceRule) {
        RuleFlinkSql flinkRule = new RuleFlinkSql();
        flinkRule.setId(complianceRule.getId());
        flinkRule.setCategoryId(complianceRule.getCategoryId());
        flinkRule.setName(complianceRule.getCode());
        flinkRule.setVersion(complianceRule.getVersion());
        flinkRule.setParams(complianceRule.getParams());
        flinkRule.setParamsDesc(complianceRule.getParamsDesc());
        Category category = com.gantenx.raffles.config.Category.getCategory(complianceRule.getCategoryId());
        if (category != null) {
            flinkRule.setCategoryName(category.getName());
            flinkRule.setBizType(BizConfigManager.getBizType(category.getName()));
        }
        int sqlTemplateId = complianceRule.getSqlTemplateId();
        SqlTemplate feature = sqlTemplateDao.selectById(sqlTemplateId);
        if (Objects.isNull(feature)) {
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
