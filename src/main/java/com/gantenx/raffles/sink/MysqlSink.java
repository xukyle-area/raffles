package com.gantenx.raffles.sink;

import com.gantenx.raffles.biz.BizConfig;
import com.gantenx.raffles.biz.BizConfigManager;
import com.gantenx.raffles.biz.consists.BizType;
import com.gantenx.raffles.biz.consists.DataSourceType;
import com.gantenx.raffles.model.RuleFlinkSql;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MysqlSink implements SinkService {

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.MYSQL;
    }

    @Override
    public void sink(StreamTableEnvironment ste, Table table, RuleFlinkSql rule) {
        BizType bizType = rule.getBizType();
        BizConfig.SinkConfig sinkConfig = BizConfigManager.getSinkConfig(bizType);
        this.checkType(sinkConfig);
        log.info("sink to mysql not implemented yet");
    }
}
