package com.gantenx.raffles.sourcer;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import com.gantenx.raffles.config.CategoryConfig;
import com.gantenx.raffles.config.consists.DataType;
import com.gantenx.raffles.model.FlinkRule;

public interface AbstractSourcer {

    public abstract DataType getDataType();

    public abstract void source(StreamExecutionEnvironment env, StreamTableEnvironment ste, FlinkRule rule);

    public default void checkType(CategoryConfig.DataTypeConfig sourceConfig) {
        if (!this.getDataType().equals(sourceConfig.getDataType())) {
            throw new RuntimeException("source type not match, expect: " + this.getDataType().getCode() + ", actual: "
                    + sourceConfig.getDataType().getCode());
        }
    }
}
