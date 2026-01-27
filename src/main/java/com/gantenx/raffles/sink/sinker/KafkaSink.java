package com.gantenx.raffles.sink.sinker;

import java.io.Serializable;
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
import com.gantenx.raffles.config.consists.DataType;
import com.gantenx.raffles.model.FlinkRule;
import com.gantenx.raffles.sink.Sinker;
import com.gantenx.raffles.sink.builder.AbstractSinkBuilder;
import com.gantenx.raffles.sink.builder.SimpleSinkBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaSink implements AbstractSinker, Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Category, AbstractSinkBuilder> sinkMap = new HashMap<>();

    public KafkaSink() {
        sinkMap.put(Category.CALCULATE, new SimpleSinkBuilder<>(CalculateOutput.class));
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
        Sinker sinkFunction =
                new Sinker(rule, sinkMap.get(rule.getCategory()), kafkaConfig.getServers(), kafkaConfig.getTopic());
        ste.toRetractStream(table, Row.class).addSink(sinkFunction).name(rule.getName() + "_sink");
    }
}
