package com.gantenx.raffles.source;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import com.gantenx.raffles.config.CategoryConfig;
import com.gantenx.raffles.config.consists.DataType;
import com.gantenx.raffles.model.FlinkRule;

public interface SourceService {
    DataType getDataType();

    void source(StreamExecutionEnvironment env, StreamTableEnvironment ste, FlinkRule rule);

    default void checkType(CategoryConfig.DataTypeConfig sourceConfig) {
        if (!this.getDataType().equals(sourceConfig.getDataType())) {
            throw new RuntimeException("source type not match, expect: " + this.getDataType().getCode() + ", actual: "
                    + sourceConfig.getDataType().getCode());
        }
    }
}
