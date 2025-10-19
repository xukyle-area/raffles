package com.gantenx.raffles.sink;

import com.gantenx.raffles.model.RuleFlinkSql;

import java.util.Map;

public interface SinkBuilder {
    Object buildSinkObject(Map<String, Object> item, RuleFlinkSql rule);
}
