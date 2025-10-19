package com.gantenx.raffles.sink;

import java.util.HashMap;
import java.util.Map;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.springframework.stereotype.Service;
import com.gantenx.raffles.config.Category;
import com.gantenx.raffles.config.CategoryConfig;
import com.gantenx.raffles.config.CategoryConfig.DataTypeConfig;
import com.gantenx.raffles.config.calculate.CalculateSinkBuilder;
import com.gantenx.raffles.config.consists.DataType;
import com.gantenx.raffles.model.RuleFlinkSql;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaSink implements SinkService {

    private final Map<Category, SinkBuilder> sinkMap = new HashMap<>();

    public KafkaSink() {
        sinkMap.put(Category.CALCULATE, new CalculateSinkBuilder());
    }

    @Override
    public DataType getDataType() {
        return DataType.KAFKA;
    }

    @Override
    public void sink(StreamTableEnvironment ste, Table table, RuleFlinkSql rule) {
        DataTypeConfig sinkConfig = rule.getCategoryConfig().getSinkConfig();
        this.checkType(sinkConfig);
        CategoryConfig.Kafka kafkaConfig = sinkConfig.getKafka();
        RuleSink sinkFunction =
                new RuleSink(rule, sinkMap.get(rule.getCategory()), kafkaConfig.getServers(), kafkaConfig.getTopic());
        ste.toRetractStream(table, Row.class).addSink(sinkFunction).name(rule.getName() + "_sink");
    }
}
