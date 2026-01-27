package com.gantenx.raffles.sink.adapter;

import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.springframework.stereotype.Service;
import com.gantenx.raffles.config.CategoryConfig;
import com.gantenx.raffles.config.consists.DataType;
import com.gantenx.raffles.model.FlinkRule;
import lombok.extern.slf4j.Slf4j;

/**
 * MySQL Sink适配器
 * 负责将Flink Table数据输出到MySQL
 */
@Slf4j
@Service
public class MysqlSinkAdapter extends SinkAdapter {
    private static final long serialVersionUID = 1L;

    @Override
    public DataType getDataType() {
        return DataType.MYSQL;
    }

    @Override
    public void sink(StreamTableEnvironment ste, Table table, FlinkRule rule) {
        CategoryConfig categoryConfig = rule.getCategoryConfig();
        CategoryConfig.DataTypeConfig sinkConfig = categoryConfig.getSinkConfig();
        this.checkType(sinkConfig);
        log.info("sink to mysql not implemented yet");
    }
}
