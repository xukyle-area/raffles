package com.gantenx.raffles.sink.sinker;

import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import com.gantenx.raffles.config.CategoryConfig;
import com.gantenx.raffles.config.consists.DataType;
import com.gantenx.raffles.model.FlinkRule;

public abstract class AbstractSinker {
    public abstract DataType getDataType();

    public abstract void sink(StreamTableEnvironment ste, Table table, FlinkRule rule);

    public void checkType(CategoryConfig.DataTypeConfig sinkConfig) {
        if (!this.getDataType().equals(sinkConfig.getDataType())) {
            throw new RuntimeException("sink type not match, expect: " + this.getDataType().getCode() + ", actual: "
                    + sinkConfig.getDataType().getCode());
        }
    }
}
