package com.gantenx.raffles.sink.adapter;

import java.io.Serializable;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import com.gantenx.raffles.config.CategoryConfig;
import com.gantenx.raffles.enums.DataType;
import com.gantenx.raffles.model.FlinkRule;

/**
 * Sink适配器抽象类
 * 负责将Flink Table转换为外部存储系统的输出
 */
public abstract class SinkAdapter implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 获取适配器支持的数据类型
     */
    public abstract DataType getDataType();

    /**
     * 将Flink Table输出到外部系统
     */
    public abstract void sink(StreamTableEnvironment ste, Table table, FlinkRule rule);

    /**
     * 检查配置类型是否匹配
     */
    public void checkType(CategoryConfig.DataTypeConfig sinkConfig) {
        if (!this.getDataType().equals(sinkConfig.getDataType())) {
            throw new RuntimeException("sink type not match, expect: " + this.getDataType().getCode() + ", actual: "
                    + sinkConfig.getDataType().getCode());
        }
    }
}
