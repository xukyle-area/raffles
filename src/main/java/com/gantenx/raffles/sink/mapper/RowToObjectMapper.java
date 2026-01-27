package com.gantenx.raffles.sink.mapper;

import java.io.Serializable;
import java.util.Map;
import com.gantenx.raffles.model.FlinkRule;

/**
 * 行数据到对象的映射器
 * 负责将Flink Row的Map表示转换为具体的业务对象
 */
public interface RowToObjectMapper extends Serializable {
    /**
     * 将Map数据构建为目标对象
     * @param item Row数据的Map表示
     * @param rule Flink规则信息
     * @return 构建的业务对象
     */
    Object buildObject(Map<String, Object> item, FlinkRule rule);
}
