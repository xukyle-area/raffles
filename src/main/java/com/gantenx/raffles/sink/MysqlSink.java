package com.gantenx.raffles.sink;


import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.springframework.stereotype.Service;
import com.gantenx.raffles.config.CategoryConfig;
import com.gantenx.raffles.config.consists.DataType;
import com.gantenx.raffles.model.FlinkRule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MysqlSink implements SinkService {

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
