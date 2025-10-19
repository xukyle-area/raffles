package com.gantenx.raffles.sink;

import com.gantenx.raffles.biz.BizConfig;
import com.gantenx.raffles.biz.BizConfigManager;
import com.gantenx.raffles.biz.consists.BizType;
import com.gantenx.raffles.biz.consists.DataSourceType;
import com.gantenx.raffles.biz.dormant.DormantSinkBuilder;
import com.gantenx.raffles.biz.ongoingcdd.OngoingCddSinkBuilder;
import com.gantenx.raffles.biz.openaccount.OpenAccountSinkBuilder;
import com.gantenx.raffles.model.RuleFlinkSql;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class KafkaSink implements SinkService {

    private final Map<BizType, SinkBuilder> sinkMap = new HashMap<>();

    public KafkaSink() {
        Set<BizType> activeBizTypes = BizConfigManager.getActiveBizTypes();
        if (activeBizTypes.contains(BizType.OPEN_ACCOUNT)) {
            log.info("OpenAccountSinkBuilder");
            sinkMap.put(BizType.OPEN_ACCOUNT, new OpenAccountSinkBuilder());
        }
        if (activeBizTypes.contains(BizType.DORMANT)) {
            log.info("DormantSinkBuilder");
            sinkMap.put(BizType.DORMANT, new DormantSinkBuilder());
        }
        if (activeBizTypes.contains(BizType.ONGOING_CDD)) {
            sinkMap.put(BizType.ONGOING_CDD, new OngoingCddSinkBuilder());
        }
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.KAFKA;
    }

    @Override
    public void sink(StreamTableEnvironment ste, Table table, RuleFlinkSql rule) {
        BizType bizType = rule.getBizType();
        BizConfig.SinkConfig sinkConfig = BizConfigManager.getSinkConfig(bizType);
        this.checkType(sinkConfig);
        BizConfig.Kafka kafkaConfig = sinkConfig.getKafka();
        RuleSink sinkFunction = new RuleSink(rule, sinkMap.get(bizType), kafkaConfig.getServers(), kafkaConfig.getTopic());
        ste.toRetractStream(table, Row.class).addSink(sinkFunction).name(rule.getCode() + "_sink");
    }
}
