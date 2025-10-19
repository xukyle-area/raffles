package com.gantenx.raffles.source;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.springframework.stereotype.Service;
import com.gantenx.raffles.config.CategoryConfig;
import com.gantenx.raffles.config.calculate.CalculateInput;
import com.gantenx.raffles.config.consists.DataType;
import com.gantenx.raffles.model.RuleFlinkSql;
import com.gantenx.raffles.utils.FindInSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaSource implements SourceService {

    private static final String OAUTH_USER_INFORMATION_TABLE = "oauth_user_information";
    private static final String GROUP_OFFSETS = "group-offsets";

    @Override
    public DataType getDataType() {
        return DataType.KAFKA;
    }

    @Override
    public void source(StreamExecutionEnvironment env, StreamTableEnvironment ste, RuleFlinkSql rule) {
        CategoryConfig categoryConfig = rule.getCategoryConfig();
        CategoryConfig.DataTypeConfig sourceConfig = categoryConfig.getSourceConfig();
        this.checkType(sourceConfig);
        KafkaTableSource kafkaTableSource = this.buildKafkaSource(sourceConfig.getKafka());
        FlinkKafkaRegister.registerKafkaTable(env, ste, kafkaTableSource, "open-account-group");
        ste.createTemporarySystemFunction("find_in_set", new FindInSet());
    }

    /**
     * 构建 kafka source
     */
    private KafkaTableSource buildKafkaSource(CategoryConfig.Kafka inputKafka) {
        KafkaTableSource kafkaTableSource = new KafkaTableSource();
        kafkaTableSource.setTable(OAUTH_USER_INFORMATION_TABLE);
        kafkaTableSource.setServers(inputKafka.getServers());
        kafkaTableSource.setTopic(inputKafka.getTopic());
        kafkaTableSource.setMaxDelay(1);
        kafkaTableSource.setStartupMode(GROUP_OFFSETS);
        kafkaTableSource.setClazz(CalculateInput.class);
        return kafkaTableSource;
    }
}
