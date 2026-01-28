package com.gantenx.raffles.sink.adapter;

import java.util.HashMap;
import java.util.Map;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.springframework.stereotype.Service;
import com.gantenx.raffles.config.Category;
import com.gantenx.raffles.config.CategoryConfig;
import com.gantenx.raffles.config.CategoryConfig.DataTypeConfig;
import com.gantenx.raffles.config.calculate.CalculateOutput;
import com.gantenx.raffles.enums.DataType;
import com.gantenx.raffles.model.FlinkRule;
import com.gantenx.raffles.sink.function.KafkaSinkFunction;
import com.gantenx.raffles.sink.mapper.ReflectionRowMapper;
import com.gantenx.raffles.sink.mapper.RowToObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka Sink适配器
 * 负责将Flink Table数据输出到Kafka
 */
@Slf4j
@Service
public class KafkaSinkAdapter extends SinkAdapter {
    private static final long serialVersionUID = 1L;

    private transient Map<Category, RowToObjectMapper> mapperRegistry;

    public KafkaSinkAdapter() {
        initMapperRegistry();
    }

    private void initMapperRegistry() {
        mapperRegistry = new HashMap<>();
        mapperRegistry.put(Category.CALCULATE, new ReflectionRowMapper<>(CalculateOutput.class));
    }

    @Override
    public DataType getDataType() {
        return DataType.KAFKA;
    }

    @Override
    public void sink(StreamTableEnvironment ste, Table table, FlinkRule rule) {
        DataTypeConfig sinkConfig = rule.getCategoryConfig().getSinkConfig();
        this.checkType(sinkConfig);
        CategoryConfig.Kafka kafkaConfig = sinkConfig.getKafka();

        // 确保 mapperRegistry 已初始化（反序列化后可能为 null）
        if (mapperRegistry == null) {
            initMapperRegistry();
        }

        RowToObjectMapper mapper = mapperRegistry.get(rule.getCategory());
        KafkaSinkFunction sinkFunction =
                new KafkaSinkFunction(rule, mapper, kafkaConfig.getServers(), kafkaConfig.getTopic());
        ste.toRetractStream(table, Row.class).addSink(sinkFunction).name(rule.getName() + "_sink");
    }
}
