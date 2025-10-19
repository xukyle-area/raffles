package com.gantenx.raffles.sink;


import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.springframework.stereotype.Service;
import com.gantenx.raffles.config.Category;
import com.gantenx.raffles.config.CategoryConfig;
import com.gantenx.raffles.config.ConfigManager;
import com.gantenx.raffles.config.consists.DataType;
import com.gantenx.raffles.model.RuleFlinkSql;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MysqlSink implements SinkService {

    @Override
    public DataType getDataType() {
        return DataType.MYSQL;
    }

    @Override
    public void sink(StreamTableEnvironment ste, Table table, RuleFlinkSql rule) {
        Category bizType = rule.getCategory();
        CategoryConfig categoryConfig = ConfigManager.getCategoryConfig(bizType);
        CategoryConfig.DataTypeConfig sinkConfig = categoryConfig.getSinkConfig();
        this.checkType(sinkConfig);
        log.info("sink to mysql not implemented yet");
    }
}
