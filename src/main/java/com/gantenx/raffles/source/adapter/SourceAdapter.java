package com.gantenx.raffles.source.adapter;

import java.io.Serializable;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import com.gantenx.raffles.config.CategoryConfig;
import com.gantenx.raffles.enums.DataType;
import com.gantenx.raffles.model.FlinkRule;

/**
 * Source适配器抽象类
 * 负责从外部数据源注册数据到Flink环境
 */
public abstract class SourceAdapter implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 获取适配器支持的数据类型
     */
    public abstract DataType getDataType();

    /**
     * 注册数据源到Flink环境
     */
    public abstract void source(StreamExecutionEnvironment env, StreamTableEnvironment ste, FlinkRule rule);

    /**
     * 检查配置类型是否匹配
     */
    public void checkType(CategoryConfig.DataTypeConfig sourceConfig) {
        if (!this.getDataType().equals(sourceConfig.getDataType())) {
            throw new RuntimeException("source type not match, expect: " + this.getDataType().getCode() + ", actual: "
                    + sourceConfig.getDataType().getCode());
        }
    }
}
