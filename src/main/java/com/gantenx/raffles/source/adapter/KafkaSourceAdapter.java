package com.gantenx.raffles.source.adapter;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.springframework.stereotype.Service;
import com.gantenx.raffles.config.CategoryConfig;
import com.gantenx.raffles.config.calculate.CalculateInput;
import com.gantenx.raffles.enums.DataType;
import com.gantenx.raffles.kafka.KafkaRegisterUtils;
import com.gantenx.raffles.kafka.KafkaTableSource;
import com.gantenx.raffles.model.FlinkRule;
import com.gantenx.raffles.utils.FindInSet;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka Source适配器
 * 负责从Kafka注册数据源到Flink环境
 */
@Slf4j
@Service
public class KafkaSourceAdapter extends SourceAdapter {
    private static final long serialVersionUID = 1L;

    private static final String KAFKA_TABLE = "calculate_input";
    private static final String GROUP_OFFSETS = "group-offsets";

    @Override
    public DataType getDataType() {
        return DataType.KAFKA;
    }

    @Override
    public void source(StreamExecutionEnvironment env, StreamTableEnvironment ste, FlinkRule rule) {
        CategoryConfig categoryConfig = rule.getCategoryConfig();
        CategoryConfig.DataTypeConfig sourceConfig = categoryConfig.getSourceConfig();
        this.checkType(sourceConfig);

        log.info("=== Kafka Source Configuration ===");
        log.info("Kafka servers from config: {}", sourceConfig.getKafka().getServers());
        log.info("Kafka topic from config: {}", sourceConfig.getKafka().getTopic());

        KafkaTableSource kafkaTableSource = this.buildKafkaSource(sourceConfig.getKafka());

        log.info("KafkaTableSource servers: {}", kafkaTableSource.getServers());
        log.info("KafkaTableSource topic: {}", kafkaTableSource.getTopic());

        KafkaRegisterUtils.registerKafkaTable(env, ste, kafkaTableSource, "open-account-group");
        ste.createTemporarySystemFunction("find_in_set", new FindInSet());
    }

    /**
     * 构建 Kafka source
     */
    private KafkaTableSource buildKafkaSource(CategoryConfig.Kafka inputKafka) {
        KafkaTableSource kafkaTableSource = new KafkaTableSource();
        kafkaTableSource.setTable(KAFKA_TABLE);
        kafkaTableSource.setServers(inputKafka.getServers());
        kafkaTableSource.setTopic(inputKafka.getTopic());
        kafkaTableSource.setMaxDelay(1);
        kafkaTableSource.setStartupMode(GROUP_OFFSETS);
        kafkaTableSource.setClazz(CalculateInput.class);
        return kafkaTableSource;
    }
}
