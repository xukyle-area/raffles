package com.gantenx.raffles.source;

import com.gantenx.raffles.biz.BizConfig;
import com.gantenx.raffles.biz.consists.DataSourceType;
import com.gantenx.raffles.model.RuleFlinkSql;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public interface SourceService {
    DataSourceType getDataSourceType();

    void source(StreamExecutionEnvironment env, StreamTableEnvironment ste, RuleFlinkSql rule);

    default void checkType(BizConfig.SourceConfig sourceConfig) {
        if (!this.getDataSourceType().getCode().equals(sourceConfig.getType())) {
            throw new RuntimeException("source type not match, expect: " + this.getDataSourceType().getCode() + ", actual: " + sourceConfig.getType());
        }
    }
}
