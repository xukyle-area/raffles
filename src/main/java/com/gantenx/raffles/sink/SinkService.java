package com.gantenx.raffles.sink;

import com.gantenx.raffles.biz.BizConfig;
import com.gantenx.raffles.biz.consists.DataSourceType;
import com.gantenx.raffles.model.RuleFlinkSql;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public interface SinkService {
    DataSourceType getDataSourceType();

    void sink(StreamTableEnvironment ste, Table table, RuleFlinkSql rule);

    default void checkType(BizConfig.SinkConfig sinkConfig) {
        if (!this.getDataSourceType().getCode().equals(sinkConfig.getType())) {
            throw new RuntimeException("sink type not match, expect: " + this.getDataSourceType().getCode() + ", actual: " + sinkConfig.getType());
        }
    }
}
