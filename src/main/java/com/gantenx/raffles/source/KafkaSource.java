package com.gantenx.raffles.source;

import com.gantenx.raffles.biz.BizConfig;
import com.gantenx.raffles.biz.BizConfigManager;
import com.gantenx.raffles.biz.consists.BizType;
import com.gantenx.raffles.biz.consists.DataSourceType;
import com.gantenx.raffles.biz.openaccount.OpenAccountInput;
import com.gantenx.raffles.model.RuleFlinkSql;
import com.gantenx.raffles.util.FindInSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaSource implements SourceService {

    private static final String OAUTH_USER_INFORMATION_TABLE = "oauth_user_information";
    private static final String GROUP_OFFSETS = "group-offsets";

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.KAFKA;
    }

    @Override
    public void source(StreamExecutionEnvironment env, StreamTableEnvironment ste, RuleFlinkSql rule) {
        BizType bizType = rule.getBizType();
        BizConfig.SourceConfig sourceConfig = BizConfigManager.getSourceConfig(bizType);
        this.checkType(sourceConfig);
        KafkaTableSource kafkaTableSource = this.buildKafkaSource(sourceConfig.getKafka());
        FlinkKafkaRegister.registerKafkaTable(env, ste, kafkaTableSource, "open-account-group");
        ste.createTemporarySystemFunction("find_in_set", new FindInSet());
    }

    /**
     * 构建 kafka source
     */
    private KafkaTableSource buildKafkaSource(BizConfig.Kafka inputKafka) {
        KafkaTableSource kafkaTableSource = new KafkaTableSource();
        kafkaTableSource.setTable(OAUTH_USER_INFORMATION_TABLE);
        kafkaTableSource.setServers(inputKafka.getServers());
        kafkaTableSource.setTopic(inputKafka.getTopic());
        kafkaTableSource.setMaxDelay(1);
        kafkaTableSource.setStartupMode(GROUP_OFFSETS);
        kafkaTableSource.setClazz(OpenAccountInput.class);
        return kafkaTableSource;
    }
}
