package com.gantenx.raffles.config.calculate;

import java.io.Serializable;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import com.gantenx.raffles.model.RuleFlinkSql;
import com.gantenx.raffles.sink.SinkBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CalculateSinkBuilder implements SinkBuilder, Serializable {

    @Override
    public Object buildSinkObject(Map<String, Object> item, RuleFlinkSql rule) {
        CalculateOutput ruleScoreOutput = new CalculateOutput();
        long multiplyResult = MapUtils.getLongValue(item, "multiplyResult");
        ruleScoreOutput.setMultiplyResult(multiplyResult);
        long addResult = MapUtils.getLongValue(item, "addResult");
        ruleScoreOutput.setAddResult(addResult);
        return ruleScoreOutput;
    }
}
