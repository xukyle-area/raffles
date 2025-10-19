package com.gantenx.raffles.biz.openaccount;

import com.gantenx.raffles.model.RuleFlinkSql;
import com.gantenx.raffles.sink.SinkBuilder;
import com.gantenx.raffles.util.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Slf4j
public class OpenAccountSinkBuilder implements SinkBuilder, Serializable {

    @Override
    public Object buildSinkObject(Map<String, Object> item, RuleFlinkSql rule) {
        OpenAccountOutput ruleScoreOutput = new OpenAccountOutput();
        String account = MapUtils.getString(item, "account");
        ruleScoreOutput.setAccount(account);
        double score = MapUtils.getDoubleValue(item, "score");
        ruleScoreOutput.setScore(score);
        ruleScoreOutput.setRuleCode(rule.getCode());
        ruleScoreOutput.setRuleExpr(rule.getExecutableSql());
        ruleScoreOutput.setCustomerId(MapUtils.getString(item, "transaction_id"));
        ruleScoreOutput.setCreateTime(System.currentTimeMillis());
        ruleScoreOutput.setHitDesc(this.getHitDesc(score, rule.getParams(), rule.getParamsDesc()));

        return ruleScoreOutput;
    }

    private String getHitDesc(double score, String params, String paramsDesc) {
        try {
            if (StringUtils.isNotEmpty(params)) {
                Map<String, Object> paramMap = GsonUtils.toMap(params);
                String key = StringUtils.EMPTY;
                for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                    if (entry.getValue() != null) {
                        String valueStr = entry.getValue().toString();
                        if (StringUtils.isNotEmpty(valueStr)) {
                            if (BigDecimal.valueOf(Double.parseDouble(valueStr)).compareTo(BigDecimal.valueOf(score)) == 0) {
                                key = entry.getKey();
                                break;
                            }
                        }
                    }
                }
                String hitDesc = StringUtils.EMPTY;
                if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(paramsDesc)) {
                    Map<String, Object> paramsDescMap = GsonUtils.toMap(paramsDesc);
                    hitDesc = String.valueOf(paramsDescMap.get(key));
                }
                if (StringUtils.isEmpty(hitDesc)) {
                    return key;
                }
                return hitDesc;
            }
        } catch (Exception e) {
            log.error("getHitDesc {}", e.getMessage(), e);
        }
        return StringUtils.EMPTY;
    }
}
